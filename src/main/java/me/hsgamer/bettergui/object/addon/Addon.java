package me.hsgamer.bettergui.object.addon;

import me.hsgamer.bettergui.BetterGUI;
import org.bukkit.command.defaults.BukkitCommand;

public abstract class Addon {

  private AddonDescription description;

  public abstract void onEnable();

  public abstract void onDisable();

  protected BetterGUI getPlugin() {
    return BetterGUI.getInstance();
  }

  public AddonDescription getDescription() {
    return description;
  }

  public void setDescription(AddonDescription description) {
    this.description = description;
  }

  public void registerCommand(BukkitCommand command) {
    getPlugin().getCommandManager().register(command);
  }
}
