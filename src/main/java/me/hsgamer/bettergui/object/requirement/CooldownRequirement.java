package me.hsgamer.bettergui.object.requirement;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.LocalVariable;
import me.hsgamer.bettergui.object.LocalVariableManager;
import me.hsgamer.bettergui.object.Requirement;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.ExpressionUtils;
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
  public String getReplacement(Player executor, String identifier) {
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
      CommonUtils.sendMessage(player,
          BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.INVALID_NUMBER)
              .replace("{input}", parsed));
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

  private long getCooldown(Player player) {
    UUID uuid = player.getUniqueId();
    if (cooldownMap.containsKey(uuid)) {
      return Instant.now().until(cooldownMap.get(uuid), ChronoUnit.MILLIS);
    } else {
      return 0;
    }
  }
}
