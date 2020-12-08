package me.hsgamer.bettergui.requirement;

import me.hsgamer.bettergui.api.requirement.BaseRequirement;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.manager.PluginVariableManager;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.expression.ExpressionUtils;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.Bukkit;

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
      int divide;

      switch (original.toLowerCase()) {
        case "_s":
        case "_seconds":
          divide = 1000;
          break;
        case "_m":
        case "_minutes":
          divide = 60000;
          break;
        case "_h":
        case "_hours":
          divide = 3600000;
          break;
        default:
          divide = 1;
          break;
      }

      return String.valueOf(millis / divide);
    });
  }

  @Override
  public Duration getParsedValue(UUID uuid) {
    String parsed = VariableManager.setVariables(String.valueOf(value).trim(), uuid);
    return Optional.ofNullable(ExpressionUtils.getResult(parsed))
      .map(bigDecimal -> Duration.ofMillis((long) bigDecimal.doubleValue() * 1000))
      .orElseGet(() -> {
        Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> MessageUtils.sendMessage(player, MessageConfig.INVALID_NUMBER.getValue().replace("{input}", parsed)));
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
