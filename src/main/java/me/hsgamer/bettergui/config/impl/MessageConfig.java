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

  public String get(DefaultMessage defaultMessage) {
    return get(String.class, defaultMessage.path, defaultMessage.def);
  }

  public enum DefaultMessage {
    PREFIX("prefix", "&f[&bBetterGUI&f] "),
    NO_PERMISSION("no-permission", "&cYou don't have permission to do this"),
    PLAYER_ONLY("player-only", "&cYou should be a player to do this"),
    SUCCESS("success", "&aSuccess"),
    MENU_REQUIRED("menu-required", "&cYou should specify a menu"),
    MENU_NOT_FOUND("menu-not-found", "&cThat menu does not exist"),
    COOLDOWN_MESSAGE("cooldown-message",
        "&cWait for {cooldown_second} secs ({cooldown}) before clicking again"),
    NO_REQUIREMENT("no-requirement", "&cYou don't meet the requirement to do this"),
    NO_EXP("no-exp", "&cYou don't have enough xp to do this"),
    HAVE_MET_REQUIREMENT_PLACEHOLDER("have-met-requirement-placeholder", "Yes"),
    INVALID_REQUIRED_ITEM("invalid-required-item",
        "&cUnable to get required item. Inform the staff"),
    NO_REQUIRED_ITEM("no-required-item",
        "&cYou must have &e{amount}x {item} for this."),
    NO_ICON_PERMISSION("no-icon-permission",
        "&cYou don't have permission to do this"),
    INVALID_NUMBER("invalid-number", "&cError converting! {input} is not a valid number"),
    INVALID_AMOUNT("invalid-amount", "&cInvalid amount of {input}! Will be set to 1 by default"),
    INVALID_CONDITION("invalid-condition", "&cInvalid condition! Please inform the staff");
    final String path;
    final Object def;

    DefaultMessage(String path, Object def) {
      this.path = path;
      this.def = def;
    }
  }
}
