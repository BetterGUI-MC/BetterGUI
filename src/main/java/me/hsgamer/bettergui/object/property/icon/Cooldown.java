package me.hsgamer.bettergui.object.property.icon;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.IconProperty;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class Cooldown extends IconProperty<ConfigurationSection> {

  private final Map<ClickType, Long> cooldownTimePerType = new EnumMap<>(ClickType.class);
  private final Map<ClickType, Map<UUID, Long>> cooldownListPerType = new EnumMap<>(
      ClickType.class);
  private final Map<UUID, Long> defaultCooldownList = new HashMap<>();
  private long defaultCooldownTime = 0;
  private String cooldownMessage;

  public Cooldown(Icon icon) {
    super(icon);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    ConfigurationSection section = getValue();
    if (section.isConfigurationSection("")) {
      // PER CLICK TYPE
      for (ClickType type : ClickType.values()) {
        String subsection = type.name();
        if (section.isSet(subsection)) {
          long cooldown = (long) (section.getDouble(subsection) * 1000);
          setTime(cooldown, type);
        }
      }
      // DEFAULT
      if (section.isSet("DEFAULT")) {
        long cooldown = (long) (section.getDouble("DEFAULT") * 1000);
        setDefaultTime(cooldown);
      }
    } else if (section.isSet("")) {
      long cooldown = (long) (section.getDouble("") * 1000);
      setDefaultTime(cooldown);
    }
    ConfigurationSection parent = section.getParent();
    parent.getKeys(false).forEach(path -> {
      if (path.equalsIgnoreCase("COOLDOWN-MESSAGE")) {
        setCooldownMessage(CommonUtils.colorize(section.getParent().getString(path)));
      }
    });
  }

  public boolean isCooldown(Player player, ClickType clickType) {
    long now = System.currentTimeMillis();
    Map<UUID, Long> cooldownList = cooldownListPerType
        .getOrDefault(clickType, defaultCooldownList);
    Long cooldownUntil = cooldownList.get(player.getUniqueId());
    long time = cooldownTimePerType.getOrDefault(clickType, defaultCooldownTime);
    if (time > 0 && cooldownUntil != null && cooldownUntil > now) {
      if (cooldownMessage != null) {
        if (!cooldownMessage.isEmpty()) {
          player.sendMessage(
              cooldownMessage
                  .replace("{cooldown}", String.valueOf(cooldownUntil - now))
                  .replace("{cooldown_second}", String.valueOf((cooldownUntil - now) / 1000))
          );
        }
      } else {
        String message = BetterGUI.getInstance().getMessageConfig()
            .get(DefaultMessage.COOLDOWN_MESSAGE);
        if (!message.isEmpty()) {
          CommonUtils.sendMessage(player, message
              .replace("{cooldown}", String.valueOf(cooldownUntil - now))
              .replace("{cooldown_second}", String.valueOf((cooldownUntil - now) / 1000))
          );
        }
      }
      return true;
    } else {
      return false;
    }
  }

  public void startCooldown(Player player, ClickType clickType) {
    long now = System.currentTimeMillis();
    Map<UUID, Long> cooldownList = cooldownListPerType
        .getOrDefault(clickType, defaultCooldownList);
    long time = cooldownTimePerType.getOrDefault(clickType, defaultCooldownTime);
    cooldownList.put(player.getUniqueId(), now + time);
  }

  public void setTime(long time, ClickType clickType) {
    this.cooldownTimePerType.put(clickType, time);
    this.cooldownListPerType.put(clickType, new HashMap<>());
  }

  public void setDefaultTime(long time) {
    this.defaultCooldownTime = time;
  }

  public void setCooldownMessage(String cooldownMessage) {
    this.cooldownMessage = cooldownMessage;
  }
}
