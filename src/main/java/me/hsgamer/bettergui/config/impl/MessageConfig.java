package me.hsgamer.bettergui.config.impl;

import me.hsgamer.bettergui.config.ConfigPath;
import me.hsgamer.bettergui.config.PluginConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class MessageConfig extends PluginConfig {

  public static final ConfigPath<String> PREFIX = new ConfigPath<>(String.class, "prefix",
      "&f[&bBetterGUI&f] ");
  public static final ConfigPath<String> NO_PERMISSION = new ConfigPath<>(String.class,
      "no-permission", "&cYou don't have permission to do this");
  public static final ConfigPath<String> PLAYER_ONLY = new ConfigPath<>(String.class, "player-only",
      "&cYou should be a player to do this");
  public static final ConfigPath<String> SUCCESS = new ConfigPath<>(String.class, "success",
      "&aSuccess");
  public static final ConfigPath<String> MENU_REQUIRED = new ConfigPath<>(String.class,
      "menu-required", "&cYou should specify a menu");
  public static final ConfigPath<String> MENU_NOT_FOUND = new ConfigPath<>(String.class,
      "menu-not-found", "&cThat menu does not exist");
  public static final ConfigPath<String> HAVE_MET_REQUIREMENT_PLACEHOLDER = new ConfigPath<>(
      String.class, "have-met-requirement-placeholder", "Yes");
  public static final ConfigPath<String> INVALID_NUMBER = new ConfigPath<>(String.class,
      "invalid-number", "&cError converting! {input} is not a valid number");
  public static final ConfigPath<String> INVALID_CONDITION = new ConfigPath<>(String.class,
      "invalid-condition", "&cInvalid condition! Please inform the staff");
  public static final ConfigPath<String> INVALID_FLAG = new ConfigPath<>(String.class,
      "invalid-flag", "&cCannot find flag '{input}'. Inform the staff");
  public static final ConfigPath<String> INVALID_ENCHANTMENT = new ConfigPath<>(String.class,
      "invalid-enchantment", "&cError parsing enchantment! {input} is not a valid enchantment");
  public static final ConfigPath<String> PLAYER_NOT_FOUND = new ConfigPath<>(String.class,
      "player-not-found",
      "&cThe player is not found. Maybe he is offline or didn't join your server");
  public static final ConfigPath<String> EMPTY_ARG_VALUE = new ConfigPath<>(String.class,
      "empty-arg-value", "/empty/");

  public MessageConfig(JavaPlugin plugin) {
    super(plugin, "messages.yml");
    getConfig().options().copyDefaults(true);
    setDefaultPath();
    saveConfig();
  }

  private void setDefaultPath() {
    PREFIX.setConfig(this);
    NO_PERMISSION.setConfig(this);
    PLAYER_ONLY.setConfig(this);
    PLAYER_NOT_FOUND.setConfig(this);
    SUCCESS.setConfig(this);
    MENU_REQUIRED.setConfig(this);
    MENU_NOT_FOUND.setConfig(this);
    HAVE_MET_REQUIREMENT_PLACEHOLDER.setConfig(this);
    INVALID_NUMBER.setConfig(this);
    INVALID_CONDITION.setConfig(this);
    INVALID_FLAG.setConfig(this);
    INVALID_ENCHANTMENT.setConfig(this);
    EMPTY_ARG_VALUE.setConfig(this);
  }
}
