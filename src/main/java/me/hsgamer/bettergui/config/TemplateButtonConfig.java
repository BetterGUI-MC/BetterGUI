package me.hsgamer.bettergui.config;

import me.hsgamer.hscore.bukkit.config.PluginConfig;
import org.bukkit.plugin.Plugin;

public class TemplateButtonConfig extends PluginConfig {
  public TemplateButtonConfig(Plugin plugin) {
    super(plugin, "template-buttons.yml");
  }
}
