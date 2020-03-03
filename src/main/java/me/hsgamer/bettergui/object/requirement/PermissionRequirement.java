package me.hsgamer.bettergui.object.requirement;

import java.util.List;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.entity.Player;

public class PermissionRequirement extends IconRequirement<Object, List<String>> {

  public PermissionRequirement(Icon icon) {
    super(icon, false);
  }

  @Override
  public List<String> getParsedValue(Player player) {
    List<String> list = CommonUtils.createStringListFromObject(value, true);
    list.replaceAll(s -> icon.hasVariables(s) ? icon.setVariables(s, player) : s);
    return list;
  }

  @Override
  public boolean check(Player player) {
    for (String value : getParsedValue(player)) {
      if (!hasPermission(player, value)) {
        sendFailCommand(player);
        return false;
      }
    }
    return true;
  }

  private boolean hasPermission(Player player, String permission) {
    if (permission.startsWith("-")) {
      return !player.hasPermission(permission.substring(1).trim());
    } else {
      return player.hasPermission(permission);
    }
  }

  @Override
  public void take(Player player) {
    // IGNORED
  }
}
