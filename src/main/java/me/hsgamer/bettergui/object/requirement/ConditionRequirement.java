package me.hsgamer.bettergui.object.requirement;

import java.util.ArrayList;
import java.util.List;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.util.ExpressionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ConditionRequirement extends IconRequirement<Boolean> {

  public ConditionRequirement(Icon icon) {
    super(icon, false);
  }

  @Override
  public List<Boolean> getParsedValue(Player player) {
    List<Boolean> list = new ArrayList<>();
    values.forEach(value -> {
      String parsed = icon.hasVariables(value) ? icon.setVariables(value, player) : value;
      if (!ExpressionUtils.isBoolean(parsed)) {
        player.sendMessage(ChatColor.RED + "Invalid condition! Please inform the staff");
        list.add(false);
      } else {
        list.add(ExpressionUtils.getResult(parsed).intValue() == 1);
      }
    });
    return list;
  }

  @Override
  public boolean check(Player player) {
    if (getParsedValue(player).contains(Boolean.FALSE)) {
      if (failMessage != null) {
        if (!failMessage.isEmpty()) {
          player.sendMessage(failMessage);
        }
      } else {
        // TODO: Config
//        if (!ChestCommands.getLang().default_no_requirement_message.isEmpty()) {
//          player.sendMessage(ChestCommands.getLang().default_no_requirement_message);
//        }
      }
      return false;
    }
    return true;
  }

  @Override
  public void take(Player player) {
    // IGNORED
  }
}
