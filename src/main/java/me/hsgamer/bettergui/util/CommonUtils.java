package me.hsgamer.bettergui.util;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;

public class CommonUtils {

  public static String colorize(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }
    return ChatColor.translateAlternateColorCodes('&', input);
  }

  public static List<String> colorize(List<String> input) {
    if (input == null || input.isEmpty()) {
      return input;
    }
    List<String> colorized = new ArrayList<>();
    input.forEach(string -> colorized.add(colorize(string)));
    return colorized;
  }

  public static boolean isValidPositiveInteger(String input) {
    try {
      return Integer.parseInt(input) > 0;
    } catch (NumberFormatException ex) {
      return false;
    }
  }
}
