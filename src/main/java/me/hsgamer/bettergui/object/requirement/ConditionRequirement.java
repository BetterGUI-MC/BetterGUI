package me.hsgamer.bettergui.object.requirement;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.ExpressionUtils;
import me.hsgamer.bettergui.util.TestCase;
import org.bukkit.entity.Player;

public class ConditionRequirement extends IconRequirement<Object, Boolean> {

  public ConditionRequirement(Icon icon) {
    super(icon, false);
  }

  @Override
  public Boolean getParsedValue(Player player) {
    TestCase<String> testCase = new TestCase<String>()
        .setPredicate(ExpressionUtils::isBoolean)
        .setSuccessNextTestCase(new TestCase<String>()
            .setPredicate(s1 -> ExpressionUtils.getResult(s1).intValue() == 1))
        .setFailConsumer(s -> CommonUtils.sendMessage(player,
            BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.INVALID_CONDITION)
                .replace("{input}", s)));
    for (String s : CommonUtils.createStringListFromObject(value, true)) {
      if (!testCase.setTestObject(icon.hasVariables(s) ? icon.setVariables(s, player) : s).test()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean check(Player player) {
    return TestCase.create(player)
        .setPredicate(player1 -> getParsedValue(player1).equals(Boolean.TRUE))
        .setFailConsumer(this::sendFailCommand)
        .test();
  }

  @Override
  public void take(Player player) {
    // IGNORED
  }
}
