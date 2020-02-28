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
    HAVE_MET_REQUIREMENT_PLACEHOLDER("have-met-requirement-placeholder", "Yes"),
    INVALID_NUMBER("invalid-number", "&cError converting! {input} is not a valid number"),
    INVALID_CONDITION("invalid-condition", "&cInvalid condition! Please inform the staff"),
    INVALID_FLAG("invalid-flag", "&cCannot find flag '{input}'. Inform the staff"),
    INVALID_ENCHANTMENT("invalid-enchantment",
        "Error parsing enchantment! {input} is not a valid enchantment"),
    PLAYER_NOT_FOUND("player-not-found",
        "&cThe player is not found. Maybe he is offline or didn't join your server");
    final String path;
    final Object def;

    DefaultMessage(String path, Object def) {
      this.path = path;
      this.def = def;
    }
  }
}
