package me.hsgamer.bettergui.object.addon;

import me.hsgamer.bettergui.BetterGUI;
import org.bukkit.command.defaults.BukkitCommand;

/**
 * The main class of the addon
 */
public abstract class Addon {

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
  public void onEnable() {}

  /**
   * Called when disabling the addon
   */
  public void onDisable() {}

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
}
