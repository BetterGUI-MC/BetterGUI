package me.hsgamer.bettergui.object.requirement;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.object.Requirement;
import me.hsgamer.bettergui.object.variable.LocalVariable;
import me.hsgamer.bettergui.object.variable.LocalVariableManager;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.expression.ExpressionUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CooldownRequirement extends Requirement<Object, Duration> implements LocalVariable {

  private final Map<UUID, Instant> cooldownMap = new HashMap<>();

  public CooldownRequirement() {
    super(true);
  }

  @Override
  public String getIdentifier() {
    return "cooldown";
  }

  @Override
  public LocalVariableManager<?> getInvolved() {
    return getVariableManager();
  }

  @Override
  public String getReplacement(OfflinePlayer executor, String identifier) {
    long millis = getCooldown(executor);
    millis = millis > 0 ? millis : 0;
    int divide;

    switch (identifier.toLowerCase()) {
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
  }

  @Override
  public Duration getParsedValue(Player player) {
    String parsed = parseFromString(String.valueOf(value).trim(), player);
    if (ExpressionUtils.isValidExpression(parsed)) {
      return Duration.ofMillis((long) ExpressionUtils.getResult(parsed).doubleValue() * 1000);
    } else {
      MessageUtils
          .sendMessage(player, MessageConfig.INVALID_NUMBER.getValue().replace("{input}", parsed));
      return Duration.ZERO;
    }
  }

  @Override
  public boolean check(Player player) {
    return getCooldown(player) <= 0;
  }

  @Override
  public void take(Player player) {
    Duration cooldownTime = getParsedValue(player);
    if (!(cooldownTime.isNegative() || cooldownTime.isZero())) {
      cooldownMap.put(player.getUniqueId(), Instant.now().plus(cooldownTime));
    }
  }

  private long getCooldown(OfflinePlayer player) {
    UUID uuid = player.getUniqueId();
    if (cooldownMap.containsKey(uuid)) {
      return Instant.now().until(cooldownMap.get(uuid), ChronoUnit.MILLIS);
    } else {
      return 0;
    }
  }
}
