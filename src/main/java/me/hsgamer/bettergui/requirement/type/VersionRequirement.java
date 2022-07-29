package me.hsgamer.bettergui.requirement.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.requirement.BaseRequirement;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.item.helper.VersionHelper;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.Validate;

import java.math.BigDecimal;
import java.util.UUID;

public class VersionRequirement extends BaseRequirement<Integer> {
  public VersionRequirement(RequirementBuilder.Input input) {
    super(input);
  }

  @Override
  protected Integer convert(Object value, UUID uuid) {
    String replaced = StringReplacerApplier.replace(String.valueOf(value).trim(), uuid, this);
    return Validate.getNumber(replaced)
      .map(BigDecimal::intValue)
      .orElseGet(() -> {
        MessageUtils.sendMessage(uuid, BetterGUI.getInstance().getMessageConfig().invalidNumber.replace("{input}", replaced));
        return 0;
      });
  }

  @Override
  public Result check(UUID uuid) {
    return VersionHelper.isAtLeast(getFinalValue(uuid)) ? Result.success() : Result.fail();
  }
}
