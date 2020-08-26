package me.hsgamer.bettergui.object.addon;

import me.hsgamer.bettergui.BetterGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * The main class of the addon
 */
public abstract class Addon extends me.hsgamer.hscore.bukkit.addon.object.Addon {

  /**
   * Register the command
   *
   * @param command the Command object
   */
  public final void registerCommand(Command command) {
    BetterGUI.getInstance().getCommandManager().register(command);
  }

  /**
   * Unregister the command
   *
   * @param command the Command object
   */
  public final void unregisterCommand(Command command) {
    BetterGUI.getInstance().getCommandManager().unregister(command);
  }

  /**
   * Register listener
   *
   * @param listener the listener to register
   */
  public final void registerListener(Listener listener) {
    Bukkit.getPluginManager().registerEvents(listener, getPlugin());
  }

  /**
   * Unregister listener
   *
   * @param listener the listener to unregister
   */
  public final void unregisterListener(Listener listener) {
    HandlerList.unregisterAll(listener);
  }
}
