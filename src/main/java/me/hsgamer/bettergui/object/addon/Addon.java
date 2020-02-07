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

  private PluginConfig config;
  private AddonDescription description;

  /**
   * Called when loading the addon
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
   * Create the addon's config
   */
  public void setupConfig() {
    config = new PluginConfig(getPlugin(),
        "addon" + File.separator + description.getName() + File.separator + "config.yml");
  }

  /**
   * Get the addon's config
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
      return;
    } else {
      config.reloadConfig();
    }
  }
}
