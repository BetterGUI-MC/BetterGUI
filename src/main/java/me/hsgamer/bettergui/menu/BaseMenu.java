package me.hsgamer.bettergui.menu;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.menu.StandardMenu;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.argument.ArgumentHandler;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.manager.MenuCommandManager;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.bukkit.utils.PermissionUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

/**
 * A {@link StandardMenu} with some basic features.
 * Included:
 * - <code>open-action</code>: The action to run when the menu is opened. Need to be called manually.
 * - <code>close-action</code>: The action to run when the menu is closed. Need to be called manually.
 * - <code>view-requirement</code>: The requirement to view the menu
 * - <code>close-requirement</code>: The requirement to close the menu. Need to be called manually.
 * - <code>permission</code>: The permission to view the menu
 * - <code>argument-processor</code>: The argument processor for the menu
 * - <code>command</code>: The command to open the menu
 */
public abstract class BaseMenu extends StandardMenu {
  protected final ActionApplier openActionApplier;
  protected final ActionApplier closeActionApplier;
  protected final RequirementApplier viewRequirementApplier;
  protected final RequirementApplier closeRequirementApplier;
  protected final List<Permission> permissions;
  protected final ArgumentHandler argumentHandler;

  protected BaseMenu(Config config) {
    super(config);
    openActionApplier = Optional.ofNullable(menuSettings.get("open-action"))
      .map(o -> new ActionApplier(this, o))
      .orElse(ActionApplier.EMPTY);
    closeActionApplier = Optional.ofNullable(menuSettings.get("close-action"))
      .map(o -> new ActionApplier(this, o))
      .orElse(ActionApplier.EMPTY);
    viewRequirementApplier = Optional.ofNullable(menuSettings.get("view-requirement"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .map(m -> new RequirementApplier(this, getName() + "_view", m))
      .orElse(RequirementApplier.EMPTY);
    closeRequirementApplier = Optional.ofNullable(menuSettings.get("close-requirement"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .map(m -> new RequirementApplier(this, getName() + "_close", m))
      .orElse(RequirementApplier.EMPTY);
    permissions = Optional.ofNullable(menuSettings.get("permission"))
      .map(o -> CollectionUtils.createStringListFromObject(o, true))
      .map(l -> l.stream().map(Permission::new).collect(Collectors.toList()))
      .orElseGet(() -> Collections.singletonList(new Permission(getInstance().getName().toLowerCase() + "." + getName())));
    argumentHandler = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "argument-processor", "arg-processor", "argument", "arg"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .map(m -> new ArgumentHandler(this, m))
      .orElseGet(() -> new ArgumentHandler(this, Collections.emptyMap()));

    Optional.ofNullable(menuSettings.get("command"))
      .map(o -> CollectionUtils.createStringListFromObject(o, true))
      .ifPresent(list -> {
        for (String s : list) {
          if (s.contains(" ")) {
            getInstance().getLogger().warning("Illegal characters in command '" + s + "'" + "in the menu '" + getName() + "'. Ignored");
          } else {
            BetterGUI betterGUI = getInstance();
            betterGUI.get(MenuCommandManager.class).registerMenuCommand(s, this);
          }
        }
      });
  }

  /**
   * Create the menu after checking the conditions
   *
   * @param player the player
   * @param args   the arguments
   * @param bypass if the requirement should be bypassed
   *
   * @return true if the menu is created
   *
   * @see #create(Player, String[], boolean)
   */
  protected abstract boolean createChecked(Player player, String[] args, boolean bypass);

  @Override
  public boolean create(Player player, String[] args, boolean bypass) {
    UUID uuid = player.getUniqueId();

    // Check Argument
    if (!argumentHandler.process(uuid, args).isPresent()) {
      return false;
    }

    // Check Permission
    if (!bypass && !PermissionUtils.hasAnyPermission(player, permissions)) {
      BetterGUI betterGUI = getInstance();
      MessageUtils.sendMessage(player, betterGUI.get(MessageConfig.class).getNoPermission());
      return false;
    }

    // Check Requirement
    if (!bypass) {
      Requirement.Result result = viewRequirementApplier.getResult(uuid);

      BatchRunnable batchRunnable = new BatchRunnable();
      batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
        result.applier.accept(uuid, process);
        process.next();
      });
      AsyncScheduler.get(BetterGUI.getInstance()).run(batchRunnable);

      if (!result.isSuccess) {
        return false;
      }
    }

    return createChecked(player, args, bypass);
  }

  @Override
  public List<String> tabComplete(Player player, String[] args) {
    return argumentHandler.handleTabComplete(player.getUniqueId(), args);
  }

  public ArgumentHandler getArgumentHandler() {
    return argumentHandler;
  }
}
