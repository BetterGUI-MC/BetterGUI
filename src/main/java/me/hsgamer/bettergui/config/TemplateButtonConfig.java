package me.hsgamer.bettergui.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import org.bukkit.plugin.Plugin;

public class TemplateButtonConfig extends BukkitConfig {
  public TemplateButtonConfig(Plugin plugin) {
    super(plugin, "template-buttons.yml");
  }
}
