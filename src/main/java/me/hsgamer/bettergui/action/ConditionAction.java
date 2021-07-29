package me.hsgamer.bettergui.action;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.hscore.expression.ExpressionUtils;

import java.math.BigDecimal;
import java.util.UUID;

public class ConditionAction extends BaseAction {
  /**
   * Create a new action
   *
   * @param string the action string
   */
  public ConditionAction(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(UUID uuid, TaskChain<?> taskChain) {
    String replacedString = getReplacedString(uuid);
    if (BigDecimal.ZERO.equals(ExpressionUtils.getResult(replacedString))) {
      taskChain.sync(TaskChain::abort);
    }
  }
}
