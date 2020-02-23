package me.hsgamer.bettergui.object.addon;

import java.io.File;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.PluginConfig;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The main class of the addon
 */
public abstract class Addon {

  private File dataFolder;
  private PluginConfig config;
  private AddonDescription description;

  /**
   * Called when loading the addon
   * <p>
   * WARNING: Don't use this to check if the required plugins are enabled Use "plugin-depend" option
   * from addon.yml
   *
   * @return whether the addon loaded properly
   */
  public boolean onLoad() {
    return true;
  }

  /**
   * Called when enabling the addon
   */
  public void onEnable() {
  }

  /**
   * Called when disabling the addon
   */
  public void onDisable() {
  }

  /**
   * Get the parent plugin
   *
   * @return the plugin
   */
  protected BetterGUI getPlugin() {
    return BetterGUI.getInstance();
  }

  /**
   * Get the addon's description
   *
   * @return the description
   */
  public AddonDescription getDescription() {
    return description;
  }

  public final void setDescription(AddonDescription description) {
    this.description = description;
  }

  /**
   * Register the command
   *
   * @param command the Command object
   */
  public void registerCommand(BukkitCommand command) {
    getPlugin().getCommandManager().register(command);
  }

  /**
   * Unregister the command
   *
   * @param command the Command object
   */
  public void unregisterCommand(BukkitCommand command) {
    getPlugin().getCommandManager().unregister(command);
  }

  /**
   * Unregister the command
   *
   * @param command the Command label
   */
  public void unregisterCommand(String command) {
    getPlugin().getCommandManager().unregister(command);
  }

  /**
   * Create the config
   */
  public void setupConfig() {
    config = new PluginConfig(getPlugin(), new File(getDataFolder(), "config.yml"));
  }

  /**
   * Get the config
   *
   * @return the config
   */
  public FileConfiguration getConfig() {
    if (config == null) {
      setupConfig();
    }
    return config.getConfig();
  }

  /**
   * Reload the config
   */
  public void reloadConfig() {
    if (config == null) {
      setupConfig();
    } else {
      config.reloadConfig();
    }
  }

  /**
   * Save the config
   */
  public void saveConfig() {
    if (config == null) {
      setupConfig();
    } else {
      config.saveConfig();
    }
  }

  /**
   * Get the addon's folder
   *
   * @return the directory for the addon
   */
  public File getDataFolder() {
    if (dataFolder == null) {
      dataFolder = new File(getPlugin().getDataFolder(),
          "addon" + File.separator + description.getName());
    }
    if (!dataFolder.exists()) {
      dataFolder.mkdirs();
    }
    return dataFolder;
  }
}
