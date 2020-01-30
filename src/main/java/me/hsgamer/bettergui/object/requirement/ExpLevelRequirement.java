package me.hsgamer.bettergui.object.requirement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.object.IconVariable;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.ExpressionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ExpLevelRequirement extends IconRequirement<Integer> implements IconVariable {

  private Map<UUID, List<Integer>> checked = new HashMap<>();

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
          String message = BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.NO_EXP);
          if (!message.isEmpty()) {
            CommonUtils.sendMessage(player, message.replace("{levels}", Integer.toString(expLevelsPrice)));
          }
        }
        return false;
      }
    }
    checked.put(player.getUniqueId(), values);
    return true;
  }

  @Override
  public void take(Player player) {
    checked.get(player.getUniqueId())
        .forEach(value -> player.setLevel(player.getLevel() - (value)));
    checked.remove(player.getUniqueId());
  }

  @Override
  public String getIdentifier() {
    return "require_exp";
  }

  @Override
  public Icon getIcon() {
    return this.icon;
  }

  @Override
  public String getReplacement(Player executor, String identifier) {
    List<Integer> values = getParsedValue(executor);
    if (values.isEmpty()) {
      return null;
    }
    for (Integer expLevelsPrice : values) {
      if (expLevelsPrice > 0 && executor.getLevel() < expLevelsPrice) {
        return String.valueOf(expLevelsPrice.intValue());
      }
    }
    return BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.HAVE_MET_REQUIREMENT_PLACEHOLDER);
  }
}
