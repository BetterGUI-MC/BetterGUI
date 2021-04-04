package me.hsgamer.bettergui.api.addon;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.hscore.bukkit.addon.PluginAddon;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;

public class BetterGUIAddon extends PluginAddon {
  @Override
  protected Config createConfig() {
    return new BukkitConfig(new File(getDataFolder(), "config.yml"));
  }

  /**
   * Register the command
   *
   * @param command the Command object
   */
  public final void registerCommand(Command command) {
    ((BetterGUI) getPlugin()).getCommandManager().register(command);
  }

  /**
   * Unregister the command
   *
   * @param command the Command object
   */
  public final void unregisterCommand(Command command) {
    ((BetterGUI) getPlugin()).getCommandManager().unregister(command);
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
