package me.hsgamer.bettergui.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.annotated.AnnotatedConfig;
import me.hsgamer.hscore.config.annotation.ConfigPath;
import org.bukkit.plugin.Plugin;

/**
 * The main class of the plugin
 */
public class MainConfig extends AnnotatedConfig {
  public final @ConfigPath("default-menu-type") String defaultMenuType;
  public final @ConfigPath("default-button-type") String defaultButtonType;
  public final @ConfigPath("replace-all-variables-each-check") boolean replaceAllVariables;

  public MainConfig(Plugin plugin) {
    super(new BukkitConfig(plugin, "config.yml"));

    defaultMenuType = "simple";
    defaultButtonType = "simple";
    replaceAllVariables = true;
  }
}
