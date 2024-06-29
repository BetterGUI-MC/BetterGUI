package me.hsgamer.bettergui.requirement.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.requirement.BaseRequirement;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.common.Validate;
import org.apache.commons.lang.time.DurationFormatUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownRequirement extends BaseRequirement<Duration> {
  private final Map<UUID, Instant> cooldownMap = new HashMap<>();

  public CooldownRequirement(RequirementBuilder.Input input) {
    super(input);
    getMenu().getVariableManager().register(getName(), StringReplacer.of((original, uuid) -> {
      long millis = getCooldown(uuid);
      millis = millis > 0 ? millis : 0;

      if (original.toLowerCase().startsWith("_format_")) {
        return DurationFormatUtils.formatDuration(millis, original.substring("_format_".length()));
      }

      switch (original.toLowerCase()) {
        case "_s":
        case "_seconds":
          return String.valueOf(millis / 1000);
        case "_m":
        case "_minutes":
          return String.valueOf(millis / 60000);
        case "_h":
        case "_hours":
          return String.valueOf(millis / 3600000);
        default:
          return String.valueOf(millis);
      }
    }));
  }

  @Override
  protected Duration convert(Object value, UUID uuid) {
    String replaced = StringReplacerApplier.replace(String.valueOf(value).trim(), uuid, this);
    return Validate.getNumber(replaced)
      .map(bigDecimal -> Duration.ofMillis((long) bigDecimal.doubleValue() * 1000))
      .orElseGet(() -> {
        MessageUtils.sendMessage(uuid, BetterGUI.getInstance().get(MessageConfig.class).getInvalidNumber(replaced));
        return Duration.ZERO;
      });
  }

  @Override
  protected Result checkConverted(UUID uuid, Duration value) {
    if (getCooldown(uuid) <= 0) {
      return Result.success(uuid1 -> {
        if (!value.isNegative() && !value.isZero()) {
          cooldownMap.put(uuid1, Instant.now().plus(value));
        }
      });
    } else {
      return Result.fail();
    }
  }

  private long getCooldown(UUID uuid) {
    if (cooldownMap.containsKey(uuid)) {
      return Instant.now().until(cooldownMap.get(uuid), ChronoUnit.MILLIS);
    } else {
      return 0;
    }
  }
}
