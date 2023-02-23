package me.hsgamer.bettergui.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.annotated.AnnotatedConfig;
import me.hsgamer.hscore.config.annotation.ConfigPath;
import org.bukkit.plugin.Plugin;

/**
 * The main class of the plugin
 */
public class MainConfig extends AnnotatedConfig {
  public final @ConfigPath("replace-all-variables-each-check") boolean replaceAllVariables;
  public final @ConfigPath("forced-update-inventory") boolean forcedUpdateInventory;
  public final @ConfigPath("use-modern-click-type") boolean modernClickType;
  public final @ConfigPath("use-legacy-button") boolean useLegacyButton;

  public MainConfig(Plugin plugin) {
    super(new BukkitConfig(plugin, "config.yml"));

    replaceAllVariables = true;
    forcedUpdateInventory = false;
    modernClickType = false;
    useLegacyButton = true;
  }
}
