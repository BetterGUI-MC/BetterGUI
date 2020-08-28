package me.hsgamer.bettergui.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

/**
 * Do the work with PlaceholderAPI
 */
public final class PlaceholderAPIHook {

  private static PlaceholderAPIPlugin placeholderAPI;

  private PlaceholderAPIHook() {
    // EMPTY
  }

  /**
   * Setup the plugin
   *
   * @return whether it's successful
   */
  public static boolean setupPlugin() {
    if (!Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      return false;
    }

    Plugin placeholderPlugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");

    if (placeholderPlugin == null) {
      return false;
    }

    placeholderAPI = (PlaceholderAPIPlugin) placeholderPlugin;
    return true;
  }

  /**
   * Check if PlaceholderAPI is loaded
   *
   * @return true if it is
   */
  public static boolean hasValidPlugin() {
    return placeholderAPI != null;
  }

  /**
   * Check if the message contains the placeholders
   *
   * @param message the message
   * @return true if it does
   */
  public static boolean hasPlaceholders(String message) {
    return PlaceholderAPI.containsPlaceholders(message);
  }

  /**
   * Replace the placeholders
   *
   * @param message  the message
   * @param executor the executor
   * @return the replaced message
   */
  public static String setPlaceholders(String message, OfflinePlayer executor) {
    return PlaceholderAPI.setPlaceholders(executor, message);
  }

}
