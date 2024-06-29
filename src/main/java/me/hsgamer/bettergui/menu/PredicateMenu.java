package me.hsgamer.bettergui.menu;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.menu.StandardMenu;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.argument.ArgumentHandler;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.utils.PermissionUtils;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.*;
import java.util.stream.Collectors;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class PredicateMenu extends StandardMenu {
  private final List<Pair<RequirementApplier, MenuProcess>> menuPredicateList;
  private final List<Permission> permissions;
  private final ArgumentHandler argumentHandler;

  public PredicateMenu(Config config) {
    super(config);

    permissions = Optional.ofNullable(menuSettings.get("permission"))
      .map(o -> CollectionUtils.createStringListFromObject(o, true))
      .map(l -> l.stream().map(Permission::new).collect(Collectors.toList()))
      .orElse(Collections.singletonList(new Permission(getInstance().getName().toLowerCase() + "." + getName())));

    Optional.ofNullable(menuSettings.get("command"))
      .map(o -> CollectionUtils.createStringListFromObject(o, true))
      .ifPresent(list -> {
        for (String s : list) {
          if (s.contains(" ")) {
            getInstance().getLogger().warning("Illegal characters in command '" + s + "'" + "in the menu '" + getName() + "'. Ignored");
          } else {
            getInstance().getMenuCommandManager().registerMenuCommand(s, this);
          }
        }
      });

    argumentHandler = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "argument-processor", "arg-processor", "argument", "arg"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .map(m -> new ArgumentHandler(this, m))
      .orElseGet(() -> new ArgumentHandler(this, Collections.emptyMap()));

    menuPredicateList = new ArrayList<>();
    configSettings.forEach((key, value) -> {
      if (!(value instanceof Map)) {
        return;
      }

      //noinspection unchecked
      Map<String, Object> values = new CaseInsensitiveStringMap<>((Map<String, Object>) value);
      String menu = Objects.toString(values.get("menu"), null);
      if (menu == null) {
        return;
      }
      String args = Optional.ofNullable(MapUtils.getIfFound(values, "args", "arguments", "arg", "argument")).map(Object::toString).orElse("");
      Map<String, Object> requirementValue = MapUtils.castOptionalStringObjectMap(values.get("requirement")).orElseGet(Collections::emptyMap);
      menuPredicateList.add(Pair.of(
        new RequirementApplier(this, getName() + "_" + key + "_requirement", requirementValue),
        new MenuProcess(menu, args)
      ));
    });
  }

  @Override
  public boolean create(Player player, String[] args, boolean bypass) {
    UUID uuid = player.getUniqueId();

    if (!argumentHandler.process(uuid, args).isPresent()) {
      return false;
    }

    if (!bypass && !PermissionUtils.hasAnyPermission(player, permissions)) {
      return false;
    }

    boolean isSuccessful = false;
    for (Pair<RequirementApplier, MenuProcess> pair : menuPredicateList) {
      Requirement.Result result = pair.getKey().getResult(uuid);

      BatchRunnable batchRunnable = new BatchRunnable();
      batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
        result.applier.accept(uuid, process);
        process.next();
      });
      AsyncScheduler.get(BetterGUI.getInstance()).run(batchRunnable);

      if (result.isSuccess) {
        MenuProcess menuProcess = pair.getValue();
        String[] finalArgs = StringReplacerApplier.replace(menuProcess.args, uuid, this).split("\\s+");
        BetterGUI.getInstance().getMenuManager().openMenu(menuProcess.menu, player, finalArgs, getParentMenu(uuid).orElse(null), bypass);
        isSuccessful = true;
        break;
      }
    }
    return isSuccessful;
  }

  @Override
  public List<String> tabComplete(Player player, String[] args) {
    return argumentHandler.handleTabComplete(player.getUniqueId(), args);
  }

  @Override
  public void update(Player player) {
    // EMPTY
  }

  @Override
  public void close(Player player) {
    // EMPTY
  }

  @Override
  public void closeAll() {
    // EMPTY
  }

  private static final class MenuProcess {
    private final String menu;
    private final String args;

    private MenuProcess(String menu, String args) {
      this.menu = menu;
      this.args = args;
    }
  }
}
