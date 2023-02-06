package me.hsgamer.bettergui.menu;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.argument.ArgumentHandler;
import me.hsgamer.bettergui.builder.ArgumentProcessorBuilder;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.bettergui.util.PlayerUtil;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.*;
import java.util.stream.Collectors;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class PredicateMenu extends Menu {
  private final List<Pair<RequirementApplier, MenuProcess>> menuPredicateList;
  private final List<Permission> permissions;
  private final ArgumentHandler argumentHandler;

  public PredicateMenu(Config config) {
    super(config);
    argumentHandler = new ArgumentHandler(this);
    menuPredicateList = new ArrayList<>();

    List<Permission> tempPermissions = Collections.singletonList(new Permission(getInstance().getName().toLowerCase() + "." + getName()));
    for (Map.Entry<String, Object> entry : config.getNormalizedValues(false).entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (!(value instanceof Map)) {
        continue;
      }
      //noinspection unchecked
      Map<String, Object> values = new CaseInsensitiveStringMap<>((Map<String, Object>) value);

      if (key.equalsIgnoreCase("menu-settings")) {
        tempPermissions = Optional.ofNullable(values.get("permission"))
          .map(o -> CollectionUtils.createStringListFromObject(o, true))
          .map(l -> l.stream().map(Permission::new).collect(Collectors.toList()))
          .orElse(tempPermissions);

        Optional.ofNullable(values.get("command"))
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

        Optional.ofNullable(MapUtil.getIfFound(values, "argument-processor", "arg-processor"))
          .map(o -> CollectionUtils.createStringListFromObject(o, true))
          .ifPresent(list -> {
            for (String s : list) {
              ArgumentProcessorBuilder.INSTANCE.build(s, this).ifPresent(argumentHandler::addProcessor);
            }
          });
      } else {
        String menu = Objects.toString(values.get("menu"), null);
        String args = Optional.ofNullable(MapUtil.getIfFound(values, "args", "arguments", "arg", "argument")).map(Object::toString).orElse("");
        Object requirementValue = values.get("requirement");
        if (menu == null || requirementValue == null) {
          continue;
        }
        MapUtil.castOptionalStringObjectMap(requirementValue)
          .map(m -> new RequirementApplier(this, getName() + "_" + key + "_requirement", m))
          .ifPresent(requirementApplier -> menuPredicateList.add(Pair.of(requirementApplier, new MenuProcess(menu, args))));
      }
    }

    permissions = tempPermissions;
  }

  @Override
  public boolean create(Player player, String[] args, boolean bypass) {
    UUID uuid = player.getUniqueId();

    if (!argumentHandler.process(uuid, args).isPresent()) {
      return false;
    }

    if (!bypass && !PlayerUtil.hasAnyPermission(player, permissions)) {
      return false;
    }

    boolean isSuccessful = false;
    for (Pair<RequirementApplier, MenuProcess> pair : menuPredicateList) {
      Requirement.Result result = pair.getKey().getResult(uuid);
      BetterGUI.runBatchRunnable(batchRunnable -> batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
        result.applier.accept(uuid, process);
        process.next();
      }));
      if (result.isSuccess) {
        MenuProcess menuProcess = pair.getValue();
        String[] finalArgs = StringReplacerApplier.replace(menuProcess.args, uuid, this).split("\\s+");
        BetterGUI.getInstance().getMenuManager().openMenu(menuProcess.menu, player, finalArgs, getParentMenu(uuid).orElse(null), bypass);
        isSuccessful = true;
        break;
      }
    }
    argumentHandler.onClear(uuid);
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
    argumentHandler.clearProcessors();
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
