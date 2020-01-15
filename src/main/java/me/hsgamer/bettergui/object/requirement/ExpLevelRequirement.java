package me.hsgamer.bettergui.object.requirement;

import java.util.ArrayList;
import java.util.List;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.util.ExpressionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ExpLevelRequirement extends IconRequirement<Integer> {

  public ExpLevelRequirement(Icon icon) {
    super(icon, false);
  }

  @Override
  public List<Integer> getParsedValue(Player player) {
    List<Integer> list = new ArrayList<>();
    values.forEach(value -> {
      String parsed = icon.hasVariables(value) ? icon.setVariables(value, player) : value;
      if (ExpressionUtils.isValidExpression(parsed)) {
        list.add(ExpressionUtils.getResult(parsed).intValue());
      } else {
        try {
          list.add(Integer.parseInt(parsed));
        } catch (NumberFormatException e) {
          String error =
              ChatColor.RED + "Error parsing value!" + parsed + " is not a valid number";
          player.sendMessage(error);
          BetterGUI.getInstance().getLogger().warning(error);
        }
      }
    });
    return list;
  }

  @Override
  public boolean check(Player player) {
    List<Integer> values = getParsedValue(player);
    if (values.isEmpty()) {
      return false;
    }
    for (Integer expLevelsPrice : values) {
      if (expLevelsPrice > 0 && player.getLevel() < expLevelsPrice) {
        if (failMessage != null) {
          if (!failMessage.isEmpty()) {
            player.sendMessage(
                failMessage.replace("{levels}", Integer.toString(expLevelsPrice)));
          }
        } else {
          // TODO: Config
//          if (!ChestCommands.getLang().no_exp.isEmpty()) {
//            player.sendMessage(
//                ChestCommands.getLang().no_exp
//                    .replace("{levels}", Integer.toString(expLevelsPrice)));
//          }
        }
        return false;
      }
    }
    return true;
  }

  @Override
  public void take(Player player) {
    getParsedValue(player)
        .forEach(value -> player.setLevel(player.getLevel() - (value)));
  }
}
