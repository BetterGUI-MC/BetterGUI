package me.hsgamer.bettergui.object.requirement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.hsgamer.bettergui.config.impl.MessageConfig;
import me.hsgamer.bettergui.object.LocalVariable;
import me.hsgamer.bettergui.object.LocalVariableManager;
import me.hsgamer.bettergui.object.Requirement;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.ExpressionUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ExpLevelRequirement extends Requirement<Object, Integer> implements LocalVariable {

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
      CommonUtils
          .sendMessage(player, MessageConfig.INVALID_NUMBER.getValue().replace("{input}", parsed));
      return 0;
    }
  }

  @Override
  public boolean check(Player player) {
    return checkLevel(player) != isInverted();
  }

  private boolean checkLevel(Player player) {
    int levels = getParsedValue(player);
    if (levels > 0 && player.getLevel() < levels) {
      return false;
    }
    checked.put(player.getUniqueId(), levels);
    return true;
  }

  @Override
  public void take(Player player) {
    player.setLevel(
        player.getLevel() + ((isInverted() ? 1 : -1) * checked.remove(player.getUniqueId())));
  }

  @Override
  public String getIdentifier() {
    return "require_levels";
  }

  @Override
  public LocalVariableManager<?> getInvolved() {
    return getVariableManager();
  }

  @Override
  public String getReplacement(OfflinePlayer executor, String identifier) {
    if (!executor.isOnline()) {
      return "";
    }
    Player player = executor.getPlayer();
    int level = getParsedValue(player);
    if (level > 0 && player.getLevel() < level) {
      return String.valueOf(level);
    }
    return MessageConfig.HAVE_MET_REQUIREMENT_PLACEHOLDER.getValue();
  }
}
