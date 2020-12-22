package me.hsgamer.bettergui.requirement.type;

import me.hsgamer.bettergui.api.requirement.TakableRequirement;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.manager.PluginVariableManager;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.expression.ExpressionUtils;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LevelRequirement extends TakableRequirement<Integer> {
  private final Map<UUID, Integer> checked = new HashMap<>();

  public LevelRequirement(String name) {
    super(name);
    PluginVariableManager.register(name, (original, uuid) -> {
      Player player = Bukkit.getPlayer(uuid);
      if (player == null) {
        return "";
      }
      int level = getParsedValue(uuid);
      if (level > 0 && player.getLevel() < level) {
        return String.valueOf(level);
      }
      return MessageConfig.HAVE_MET_REQUIREMENT_PLACEHOLDER.getValue();
    });
  }

  @Override
  public Integer getParsedValue(UUID uuid) {
    String parsed = VariableManager.setVariables(String.valueOf(value).trim(), uuid);
    return Optional.ofNullable(ExpressionUtils.getResult(parsed)).map(BigDecimal::intValue).orElseGet(() -> {
      MessageUtils.sendMessage(uuid, MessageConfig.INVALID_NUMBER.getValue().replace("{input}", parsed));
      return 0;
    });
  }

  @Override
  public boolean check(UUID uuid) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      return true;
    }
    int levels = getParsedValue(uuid);
    if (levels > 0 && player.getLevel() < levels) {
      return false;
    }
    checked.put(player.getUniqueId(), levels);
    return true;
  }

  @Override
  protected boolean getDefaultTake() {
    return true;
  }

  @Override
  protected Object getDefaultValue() {
    return "0";
  }

  @Override
  protected void takeChecked(UUID uuid) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      return;
    }
    player.setLevel(player.getLevel() + -checked.remove(player.getUniqueId()));
  }
}
