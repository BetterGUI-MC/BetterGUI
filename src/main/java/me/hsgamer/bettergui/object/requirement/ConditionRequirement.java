package me.hsgamer.bettergui.object.requirement;

import java.util.List;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.object.Requirement;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.CommonUtils;
import me.hsgamer.hscore.expression.ExpressionUtils;
import org.bukkit.entity.Player;

public class ConditionRequirement extends Requirement<Object, Boolean> {

  public ConditionRequirement() {
    super(false);
  }

  @Override
  public Boolean getParsedValue(Player player) {
    List<String> list = CommonUtils.createStringListFromObject(value, true);
    list.replaceAll(s -> parseFromString(s, player));
    for (String s : list) {
      if (!ExpressionUtils.isBoolean(s)) {
        MessageUtils
            .sendMessage(player, MessageConfig.INVALID_CONDITION.getValue().replace("{input}", s));
        continue;
      }
      if (ExpressionUtils.getResult(s).intValue() != 1) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean check(Player player) {
    return !getParsedValue(player).equals(isInverted());
  }

  @Override
  public void take(Player player) {
    // IGNORED
  }
}
