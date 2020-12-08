package me.hsgamer.bettergui.requirement;

import me.hsgamer.bettergui.api.requirement.BaseRequirement;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PermissionRequirement extends BaseRequirement<List<String>> {
  public PermissionRequirement(String name) {
    super(name);
  }

  @Override
  public List<String> getParsedValue(UUID uuid) {
    List<String> list = CollectionUtils.createStringListFromObject(value, true);
    list.replaceAll(s -> VariableManager.setVariables(s, uuid));
    return list;
  }

  @Override
  public boolean check(UUID uuid) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      return true;
    }
    return getParsedValue(uuid).stream().allMatch(s -> hasPermission(player, s));
  }

  private boolean hasPermission(Player player, String permission) {
    if (permission.startsWith("-")) {
      return !player.hasPermission(permission.substring(1).trim());
    } else {
      return player.hasPermission(permission);
    }
  }

  @Override
  public void take(UUID uuid) {
    // EMPTY
  }
}
