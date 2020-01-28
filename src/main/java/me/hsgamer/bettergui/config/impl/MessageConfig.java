package me.hsgamer.bettergui.config.impl;

import me.hsgamer.bettergui.config.PluginConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageConfig extends PluginConfig {

  public MessageConfig(JavaPlugin plugin) {
    super(plugin, "messages.yml");
  }
}
