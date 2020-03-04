package me.hsgamer.bettergui.object.property.icon.impl;

import co.aikar.taskchain.TaskChain;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.CommandBuilder;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.SimpleIconVariable;
import me.hsgamer.bettergui.object.property.IconProperty;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class Cooldown extends IconProperty<ConfigurationSection> {

  private final Map<ClickType, Duration> cooldownTimePerType = new EnumMap<>(ClickType.class);
  private final Map<ClickType, Map<UUID, Instant>> cooldownListPerType = new EnumMap<>(
      ClickType.class);
  private final Map<ClickType, List<Command>> commandListPerClickType = new EnumMap<>(
      ClickType.class);

  private Duration defaultCooldown = Duration.ofMillis(0);
  private final Map<UUID, Instant> defaultCooldownList = new HashMap<>();
  private final List<Command> defaultCommand = new ArrayList<>();

  public Cooldown(Icon icon) {
    super(icon);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(getValue().getValues(false));
    // PER CLICK TYPE
    for (ClickType type : ClickType.values()) {
      String subsection = type.name();
      if (keys.containsKey(subsection)) {
        createCooldown(type, keys.get(subsection));
      }
    }
    // DEFAULT
    if (keys.containsKey("default")) {
      createCooldown(null, keys.get("default"));
    }
  }

  public void createCooldown(ClickType type, Object object) {
    long time;
    List<Command> commands = new ArrayList<>();

    if (object instanceof ConfigurationSection) {
      Map<String, Object> keys = new CaseInsensitiveStringMap<>(
          ((ConfigurationSection) object).getValues(false));
      if (keys.containsKey(Settings.VALUE)) {
        time = (long) (Double.parseDouble(String.valueOf(keys.get(Settings.VALUE))) * 1000);
        if (keys.containsKey(Settings.COMMAND)) {
          commands.addAll(CommandBuilder.getCommands(getIcon(),
              CommonUtils.createStringListFromObject(keys.get(Settings.COMMAND), true)));
        }
      } else {
        return;
      }
    } else {
      time = (long) (Double.parseDouble(String.valueOf(object)) * 1000);
    }

    if (type != null) {
      setTime(time, type);
      commandListPerClickType.put(type, commands);
    } else {
      setDefaultTime(time);
      defaultCommand.addAll(commands);
    }
    registerVariable(type);
  }

  public void setTime(long time, ClickType clickType) {
    this.cooldownTimePerType.put(clickType, Duration.ofMillis(time));
    this.cooldownListPerType.put(clickType, new HashMap<>());
  }

  public void setDefaultTime(long time) {
    this.defaultCooldown = Duration.ofMillis(time);
  }

  public boolean isCooldown(Player player, ClickType clickType) {
    return getCooldown(player, clickType) > 0;
  }

  public long getCooldown(Player player, ClickType clickType) {
    UUID uuid = player.getUniqueId();
    Map<UUID, Instant> cooldownList = cooldownListPerType
        .getOrDefault(clickType, defaultCooldownList);
    if (cooldownList.containsKey(uuid)) {
      return Instant.now().until(cooldownList.get(uuid), ChronoUnit.MILLIS);
    } else {
      return 0;
    }
  }

  public void sendFailCommand(Player player, ClickType clickType) {
    TaskChain<?> taskChain = BetterGUI.newChain();
    commandListPerClickType.getOrDefault(clickType, defaultCommand)
        .forEach(command -> command.addToTaskChain(player, taskChain));
    taskChain.execute();
  }

  public void startCooldown(Player player, ClickType clickType) {
    UUID uuid = player.getUniqueId();
    Map<UUID, Instant> cooldownList = cooldownListPerType
        .getOrDefault(clickType, defaultCooldownList);
    Duration cooldownTime = cooldownTimePerType.getOrDefault(clickType, defaultCooldown);
    if (!(cooldownTime.isNegative() || cooldownTime.isZero())) {
      cooldownList.put(uuid, Instant.now().plus(cooldownTime));
    }
  }

  public void registerVariable(ClickType clickType) {
    getIcon().registerVariable(new SimpleIconVariable(getIcon()) {
      @Override
      public String getIdentifier() {
        return (clickType != null ? clickType.name().toLowerCase() : "default") + "_cooldown";
      }

      @Override
      public String getReplacement(Player executor, String identifier) {
        return String.valueOf(getCooldown(executor, clickType));
      }
    });
  }

  private static class Settings {

    static final String VALUE = "value";
    static final String COMMAND = "command";
  }
}
