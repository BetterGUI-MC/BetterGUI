package me.hsgamer.bettergui.requirement.type;

import me.hsgamer.bettergui.api.requirement.BaseRequirement;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.Validate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ConditionRequirement extends BaseRequirement<List<String>> {
  public ConditionRequirement(RequirementBuilder.Input input) {
    super(input);
  }

  @Override
  protected List<String> convert(Object value, UUID uuid) {
    List<String> list = CollectionUtils.createStringListFromObject(value, true);
    list.replaceAll(s -> StringReplacerApplier.replace(s, uuid, this));
    return list;
  }

  @Override
  protected Result checkConverted(UUID uuid, List<String> value) {
    return value.stream().allMatch(this::isTrueCondition)
      ? Result.success()
      : Result.fail();
  }

  private boolean isTrueCondition(String condition) {
    return condition.equalsIgnoreCase("true")
      || condition.equalsIgnoreCase("yes")
      || condition.equalsIgnoreCase("on")
      || Validate.getNumber(condition).map(number -> !BigDecimal.ZERO.equals(number)).orElse(false);
  }
}
