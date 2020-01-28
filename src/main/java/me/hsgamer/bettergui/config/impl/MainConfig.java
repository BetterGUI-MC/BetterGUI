package me.hsgamer.bettergui.config.impl;

import me.hsgamer.bettergui.config.PluginConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class MainConfig extends PluginConfig {

  public MainConfig(JavaPlugin plugin) {
    super(plugin, "config.yml");
  }
}
