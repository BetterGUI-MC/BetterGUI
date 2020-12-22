package me.hsgamer.bettergui.requirement.type;

import me.hsgamer.bettergui.api.requirement.BaseRequirement;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.manager.PluginVariableManager;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.expression.ExpressionUtils;
import me.hsgamer.hscore.variable.VariableManager;
import org.apache.commons.lang.time.DurationFormatUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CooldownRequirement extends BaseRequirement<Duration> {
  private final Map<UUID, Instant> cooldownMap = new HashMap<>();

  public CooldownRequirement(String name) {
    super(name);
    PluginVariableManager.register(name, (original, uuid) -> {
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
    });
  }

  @Override
  public Duration getParsedValue(UUID uuid) {
    String parsed = VariableManager.setVariables(String.valueOf(value).trim(), uuid);
    return Optional.ofNullable(ExpressionUtils.getResult(parsed))
      .map(bigDecimal -> Duration.ofMillis((long) bigDecimal.doubleValue() * 1000))
      .orElseGet(() -> {
        MessageUtils.sendMessage(uuid, MessageConfig.INVALID_NUMBER.getValue().replace("{input}", parsed));
        return Duration.ZERO;
      });
  }

  @Override
  public boolean check(UUID uuid) {
    return getCooldown(uuid) <= 0;
  }

  @Override
  public void take(UUID uuid) {
    Duration cooldownTime = getParsedValue(uuid);
    if (!cooldownTime.isNegative() && !cooldownTime.isZero()) {
      cooldownMap.put(uuid, Instant.now().plus(cooldownTime));
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
