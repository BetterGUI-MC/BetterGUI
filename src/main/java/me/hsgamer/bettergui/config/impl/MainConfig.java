package me.hsgamer.bettergui.config.impl;

import me.hsgamer.bettergui.config.ConfigPath;
import me.hsgamer.bettergui.config.PluginConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainConfig extends PluginConfig {

  public static final ConfigPath<String> DEFAULT_MENU_TYPE = new ConfigPath<>(String.class,
      "default-menu-type", "simple");
  public static final ConfigPath<String> DEFAULT_ICON_TYPE = new ConfigPath<>(String.class,
      "default-icon-type", "simple");
  public static final ConfigPath<Boolean> METRICS = new ConfigPath<>(Boolean.class, "metrics",
      true);
  public static final ConfigPath<Boolean> MODERN_CLICK_TYPE = new ConfigPath<>(Boolean.class,
      "use-modern-click-type", false);
  public static final ConfigPath<Boolean> REPLACE_ALL_VARIABLES = new ConfigPath<>(Boolean.class,
      "replace-all-variables-each-check", true);

  public MainConfig(JavaPlugin plugin) {
    super(plugin, "config.yml");
    getConfig().options().copyDefaults(true);
    setDefaultPath();
    saveConfig();
  }

  private void setDefaultPath() {
    DEFAULT_ICON_TYPE.setConfig(this);
    DEFAULT_MENU_TYPE.setConfig(this);
    METRICS.setConfig(this);
    MODERN_CLICK_TYPE.setConfig(this);
    REPLACE_ALL_VARIABLES.setConfig(this);
  }
}
