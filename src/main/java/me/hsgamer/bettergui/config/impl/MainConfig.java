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
  }
}
