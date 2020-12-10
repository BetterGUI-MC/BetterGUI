package me.hsgamer.bettergui.config;

import me.hsgamer.hscore.bukkit.config.PluginConfig;
import me.hsgamer.hscore.config.PathLoader;
import me.hsgamer.hscore.config.path.StringConfigPath;
import org.bukkit.plugin.java.JavaPlugin;

public final class MessageConfig extends PluginConfig {

  public static final StringConfigPath PREFIX = new StringConfigPath("prefix", "&f[&bBetterGUI&f] ");
  public static final StringConfigPath NO_PERMISSION = new StringConfigPath("no-permission", "&cYou don't have permission to do this");
  public static final StringConfigPath PLAYER_ONLY = new StringConfigPath("player-only", "&cYou should be a player to do this");
  public static final StringConfigPath SUCCESS = new StringConfigPath("success", "&aSuccess");
  public static final StringConfigPath MENU_REQUIRED = new StringConfigPath("menu-required", "&cYou should specify a menu");
  public static final StringConfigPath MENU_NOT_FOUND = new StringConfigPath("menu-not-found", "&cThat menu does not exist");
  public static final StringConfigPath HAVE_MET_REQUIREMENT_PLACEHOLDER = new StringConfigPath("have-met-requirement-placeholder", "Yes");
  public static final StringConfigPath INVALID_NUMBER = new StringConfigPath("invalid-number", "&cError converting! {input} is not a valid number");
  public static final StringConfigPath INVALID_CONDITION = new StringConfigPath("invalid-condition", "&cInvalid condition! Please inform the staff");
  public static final StringConfigPath INVALID_FLAG = new StringConfigPath("invalid-flag", "&cCannot find flag '{input}'. Inform the staff");
  public static final StringConfigPath INVALID_ENCHANTMENT = new StringConfigPath("invalid-enchantment", "&cError parsing enchantment! {input} is not a valid enchantment");
  public static final StringConfigPath PLAYER_NOT_FOUND = new StringConfigPath("player-not-found", "&cThe player is not found. Maybe he is offline or didn't join your server");
  public static final StringConfigPath EMPTY_ARG_VALUE = new StringConfigPath("empty-arg-value", "/empty/");

  public MessageConfig(JavaPlugin plugin) {
    super(plugin, "messages.yml");
    getConfig().options().copyDefaults(true);
    PathLoader.loadPath(this);
    saveConfig();
  }
}
