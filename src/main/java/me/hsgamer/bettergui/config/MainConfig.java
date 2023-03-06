package me.hsgamer.bettergui.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.annotated.AnnotatedConfig;
import me.hsgamer.hscore.config.annotation.ConfigPath;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.net.URI;

/**
 * The main class of the plugin
 */
public class MainConfig extends AnnotatedConfig {
  public final @ConfigPath("replace-all-variables-each-check") boolean replaceAllVariables;
  public final @ConfigPath("forced-update-inventory") boolean forcedUpdateInventory;
  public final @ConfigPath("use-modern-click-type") boolean modernClickType;
  public final @ConfigPath("use-legacy-button") boolean useLegacyButton;
  public final @ConfigPath("relative-menu-name") boolean relativeMenuName;
  public final @ConfigPath("trim-menu-file-extension") boolean trimMenuFileExtension;
  public final @ConfigPath("include-menu-in-template") boolean includeMenuInTemplate;

  public MainConfig(Plugin plugin) {
    super(new BukkitConfig(plugin, "config.yml"));

    replaceAllVariables = true;
    forcedUpdateInventory = false;
    modernClickType = false;
    useLegacyButton = true;
    relativeMenuName = false;
    trimMenuFileExtension = false;
    includeMenuInTemplate = false;
  }

  /**
   * Get the file name
   *
   * @param rootFolder the root folder
   * @param file       the file
   *
   * @return the file name
   */
  public String getFileName(File rootFolder, File file) {
    String name;
    if (relativeMenuName) {
      URI menusFolderURI = rootFolder.toURI();
      URI fileURI = file.toURI();
      name = menusFolderURI.relativize(fileURI).getPath();
    } else {
      name = file.getName();
    }
    if (trimMenuFileExtension) {
      int index = name.lastIndexOf('.');
      if (index > 0) {
        name = name.substring(0, index);
      }
    }
    return name;
  }
}
