package me.hsgamer.bettergui.menu;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.bettergui.util.PlayerUtil;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
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
  private final List<Pair<RequirementApplier, String>> menuPredicateList;
  private final List<Permission> permissions;

  public PredicateMenu(Config config) {
    super(config);
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
      } else {
        String menu = Objects.toString(values.get("menu"), null);
        Object requirementValue = values.get("requirement");
        if (menu == null || requirementValue == null) {
          continue;
        }
        MapUtil.castOptionalStringObjectMap(requirementValue)
          .map(m -> new RequirementApplier(this, getName() + "_" + key + "_requirement", m))
          .ifPresent(requirementApplier -> menuPredicateList.add(Pair.of(requirementApplier, menu)));
      }
    }

    permissions = tempPermissions;
  }

  @Override
  public boolean create(Player player, String[] args, boolean bypass) {
    UUID uuid = player.getUniqueId();
    if (!bypass && !PlayerUtil.hasAnyPermission(player, permissions)) {
      return false;
    }

    for (Pair<RequirementApplier, String> pair : menuPredicateList) {
      Requirement.Result result = pair.getKey().getResult(uuid);
      BetterGUI.runBatchRunnable(batchRunnable -> batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
        result.applier.accept(uuid, process);
        process.next();
      }));
      if (result.isSuccess) {
        BetterGUI.getInstance().getMenuManager().openMenu(pair.getValue(), player, args, getParentMenu(uuid).orElse(null), bypass);
        return true;
      }
    }
    return false;
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
}
