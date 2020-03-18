package me.hsgamer.bettergui.object.requirement;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconVariable;
import me.hsgamer.bettergui.object.Requirement;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.ExpressionUtils;
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.entity.Player;

public class ExpLevelRequirement extends Requirement<Object, Integer> implements IconVariable {

  private final Map<UUID, Integer> checked = new HashMap<>();

  public ExpLevelRequirement() {
    super(false);
  }

  @Override
  public Integer getParsedValue(Player player) {
    String parsed = parseFromString(String.valueOf(value).trim(), player);
    if (ExpressionUtils.isValidExpression(parsed)) {
      return ExpressionUtils.getResult(parsed).intValue();
    } else {
      Optional<BigDecimal> number = Validate.getNumber(parsed);
      if (number.isPresent()) {
        return number.get().intValue();
      } else {
        CommonUtils.sendMessage(player,
            BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.INVALID_NUMBER)
                .replace("{input}", parsed));
        return 0;
      }
    }
  }

  @Override
  public boolean check(Player player) {
    int levels = getParsedValue(player);
    if (levels > 0 && player.getLevel() < levels) {
      return false;
    }
    checked.put(player.getUniqueId(), levels);
    return true;
  }

  @Override
  public void take(Player player) {
    player.setLevel(player.getLevel() - checked.remove(player.getUniqueId()));
  }

  @Override
  public String getIdentifier() {
    return "require_levels";
  }

  @Override
  public Optional<Icon> getIconInvolved() {
    return getIcon();
  }

  @Override
  public String getReplacement(Player executor, String identifier) {
    int level = getParsedValue(executor);
    if (level > 0 && executor.getLevel() < level) {
      return String.valueOf(level);
    }
    return BetterGUI.getInstance().getMessageConfig()
        .get(DefaultMessage.HAVE_MET_REQUIREMENT_PLACEHOLDER);
  }
}
