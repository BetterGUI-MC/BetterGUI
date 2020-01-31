package me.hsgamer.bettergui.config.impl;

import me.hsgamer.bettergui.config.PluginConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageConfig extends PluginConfig {

  public MessageConfig(JavaPlugin plugin) {
    super(plugin, "messages.yml");
    for (DefaultMessage defaultMessage : DefaultMessage.values()) {
      getConfig().addDefault(defaultMessage.path, defaultMessage.def);
    }
    getConfig().options().copyDefaults(true);
    saveConfig();
  }

  @SuppressWarnings("unchecked")
  public <T> T get(DefaultMessage defaultMessage) {
    return get((Class<T>) defaultMessage.classType, defaultMessage.path, defaultMessage.def);
  }

  public enum DefaultMessage {
    PREFIX(String.class, "prefix", "&f[&bBetterGUI&f] "),
    NO_PERMISSION(String.class, "no-permission", "&cYou don't have permission to do this"),
    PLAYER_ONLY(String.class, "player-only", "&cYou should be a player to do this"),
    SUCCESS(String.class, "success", "&aSuccess"),
    MENU_REQUIRED(String.class, "menu-required", "&cYou should specify a menu"),
    MENU_NOT_FOUND(String.class, "menu-not-found", "&cThat menu does not exist"),
    COOLDOWN_MESSAGE(String.class, "cooldown-message",
        "&cWait for {cooldown_second} secs ({cooldown}) before clicking again"),
    NO_REQUIREMENT(String.class, "no-requirement", "&cYou don't meet the requirement to do this"),
    NO_EXP(String.class, "no-exp", "&cYou don't have enough xp to do this"),
    HAVE_MET_REQUIREMENT_PLACEHOLDER(String.class, "have-met-requirement-placeholder", "Yes"),
    INVALID_REQUIRED_ITEM(String.class, "invalid-required-item",
        "&cUnable to get required item. Inform the staff"),
    NO_REQUIRED_ITEM(String.class, "no-required-item",
        "&cYou must have &e{amount}x {item} &c(data value: {datavalue}) for this."),
    NO_ICON_PERMISSION(String.class, "no-icon-permission",
        "&cYou don't have permission to do this");
    Class<?> classType;
    String path;
    Object def;

    DefaultMessage(Class<?> classType, String path, Object def) {
      this.classType = classType;
      this.path = path;
      this.def = def;
    }
  }
}
