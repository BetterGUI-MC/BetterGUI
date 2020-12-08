package me.hsgamer.bettergui.requirement;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.manager.PluginVariableManager;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.hscore.expression.ExpressionUtils;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LevelRequirement implements Requirement {
  private final String name;
  private final Map<UUID, Integer> checked = new HashMap<>();
  private Menu menu;
  private String value = "0";
  private boolean take = true;

  public LevelRequirement(String name) {
    this.name = name;
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

  private Integer getParsedValue(UUID uuid) {
    String parsed = VariableManager.setVariables(String.valueOf(value).trim(), uuid);
    if (ExpressionUtils.isValidExpression(parsed)) {
      return ExpressionUtils.getResult(parsed).intValue();
    } else {
      Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> MessageUtils.sendMessage(player, MessageConfig.INVALID_NUMBER.getValue().replace("{input}", parsed)));
      return 0;
    }
  }

  @Override
  public Menu getMenu() {
    return menu;
  }

  @Override
  public void setMenu(Menu menu) {
    this.menu = menu;
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
  public void take(UUID uuid) {
    if (!take) {
      return;
    }
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      return;
    }
    player.setLevel(player.getLevel() + -checked.remove(player.getUniqueId()));
  }

  @Override
  public void setValue(Object value) {
    if (value instanceof ConfigurationSection) {
      Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(((ConfigurationSection) value).getValues(false));
      this.value = Optional.ofNullable(keys.get("value")).map(String::valueOf).orElse(this.value);
      this.take = Optional.ofNullable(keys.get("take")).map(String::valueOf).map(Boolean::parseBoolean).orElse(this.take);
    } else {
      this.value = String.valueOf(value);
    }
  }

  @Override
  public String getName() {
    return name;
  }
}
