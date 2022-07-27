package me.hsgamer.bettergui.requirement.type;

import me.hsgamer.bettergui.api.requirement.BaseRequirement;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PermissionRequirement extends BaseRequirement<List<String>> {
  public PermissionRequirement(RequirementBuilder.Input input) {
    super(input);
  }

  @Override
  protected List<String> convert(Object value, UUID uuid) {
    List<String> list = CollectionUtils.createStringListFromObject(value, true);
    list.replaceAll(s -> StringReplacerApplier.replace(s, uuid, this));
    return list;
  }

  @Override
  public Result check(UUID uuid) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      return Result.success();
    }
    if (getFinalValue(uuid).parallelStream().allMatch(s -> hasPermission(player, s))) {
      return Result.success();
    } else {
      return Result.fail();
    }
  }

  private boolean hasPermission(Player player, String permission) {
    if (permission.startsWith("-")) {
      return !player.hasPermission(permission.substring(1).trim());
    } else {
      return player.hasPermission(permission);
    }
  }
}
