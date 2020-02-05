package me.hsgamer.bettergui.object.requirement;

import java.util.Arrays;
import java.util.List;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.ExpressionUtils;
import org.bukkit.entity.Player;

public class ConditionRequirement extends IconRequirement<Object, Boolean> {

  public ConditionRequirement(Icon icon) {
    super(icon, false);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Boolean getParsedValue(Player player) {
    List<String> split;
    if (value instanceof String) {
      split = Arrays.asList(((String) value).split(";"));
    } else {
      split = (List<String>) value;
    }
    split.replaceAll(String::trim);
    for (String s : split) {
      String parsed = icon.hasVariables(s) ? icon.setVariables(s, player) : s;
      if (ExpressionUtils.isBoolean(parsed)) {
        if (ExpressionUtils.getResult(parsed).intValue() != 1) {
          return false;
        }
      } else {
        CommonUtils.sendMessage(player,
            BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.INVALID_CONDITION)
                .replace("{input}", s));
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean check(Player player) {
    if (getParsedValue(player).equals(Boolean.FALSE)) {
      if (failMessage != null) {
        if (!failMessage.isEmpty()) {
          player.sendMessage(failMessage);
        }
      } else {
        String message = BetterGUI.getInstance().getMessageConfig()
            .get(DefaultMessage.NO_REQUIREMENT);
        if (!message.isEmpty()) {
          CommonUtils.sendMessage(player, message);
        }
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
