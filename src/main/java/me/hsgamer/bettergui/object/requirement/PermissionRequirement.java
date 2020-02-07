package me.hsgamer.bettergui.object.requirement;

import java.util.ArrayList;
import java.util.List;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.entity.Player;

public class PermissionRequirement extends IconRequirement<List<String>, List<String>> {

  public PermissionRequirement(Icon icon) {
    super(icon, false);
  }

  @Override
  public List<String> getParsedValue(Player player) {
    List<String> list = new ArrayList<>();
    value.forEach(
        s -> list.add(icon.hasVariables(s) ? icon.setVariables(s, player) : s));
    value.replaceAll(String::trim);
    return list;
  }

  @Override
  public boolean check(Player player) {
    for (String value : getParsedValue(player)) {
      if (!hasPermission(player, value)) {
        if (failMessage != null) {
          if (!failMessage.isEmpty()) {
            player.sendMessage(failMessage.replace("{permission}", value));
          }
        } else {
          String message = BetterGUI.getInstance().getMessageConfig()
              .get(DefaultMessage.NO_ICON_PERMISSION).replace("{permission}", value);
          if (!message.isEmpty()) {
            CommonUtils.sendMessage(player, message);
          }
        }
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
