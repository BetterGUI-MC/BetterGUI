package me.hsgamer.bettergui.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPIHook {

  private static PlaceholderAPIPlugin placeholderAPI;

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

  public static boolean hasValidPlugin() {
    return placeholderAPI != null;
  }

  public static boolean hasPlaceholders(String message) {
    return PlaceholderAPI.containsPlaceholders(message);
  }

  public static String setPlaceholders(String message, Player executor) {
    return PlaceholderAPI.setPlaceholders(executor, message);
  }

}
