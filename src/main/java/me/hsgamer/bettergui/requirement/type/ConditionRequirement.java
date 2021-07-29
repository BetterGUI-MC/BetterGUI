package me.hsgamer.bettergui.requirement.type;

import me.hsgamer.bettergui.api.requirement.BaseRequirement;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.expression.ExpressionUtils;
import me.hsgamer.hscore.variable.VariableManager;

import java.math.BigDecimal;
import java.util.UUID;

public class ConditionRequirement extends BaseRequirement<Boolean> {
  public ConditionRequirement(String name) {
    super(name);
  }

  @Override
  public Boolean getParsedValue(UUID uuid) {
    return CollectionUtils.createStringListFromObject(value, true).parallelStream()
      .map(s -> VariableManager.setVariables(s, uuid))
      .noneMatch(s -> BigDecimal.ZERO.equals(ExpressionUtils.getResult(s)));
  }

  @Override
  public boolean check(UUID uuid) {
    return getParsedValue(uuid);
  }

  @Override
  public void take(UUID uuid) {
    // EMPTY
  }
}
