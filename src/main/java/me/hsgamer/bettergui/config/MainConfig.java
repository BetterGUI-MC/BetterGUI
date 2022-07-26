package me.hsgamer.bettergui.config;

import me.hsgamer.bettergui.listener.AlternativeCommandListener;
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
  public final @ConfigPath("forced-update-inventory") boolean forcedUpdateInventory;
  public final @ConfigPath("use-modern-click-type") boolean modernClickType;
  public final @ConfigPath("use-legacy-button") boolean useLegacyButton;
  public final @ConfigPath(value = "alternative-command-manager", converter = AlternativeCommandListener.SettingConverter.class) AlternativeCommandListener.Setting alternativeCommandManager;

  public MainConfig(Plugin plugin) {
    super(new BukkitConfig(plugin, "config.yml"));

    defaultMenuType = "simple";
    defaultButtonType = "simple";
    replaceAllVariables = true;
    forcedUpdateInventory = false;
    modernClickType = false;
    useLegacyButton = true;
    alternativeCommandManager = new AlternativeCommandListener.Setting();
  }
}
