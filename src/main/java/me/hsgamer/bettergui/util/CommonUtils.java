package me.hsgamer.bettergui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.hsgamer.bettergui.config.impl.MessageConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class CommonUtils {

  private CommonUtils() {

  }

  /**
   * Convert to colored string
   *
   * @param input the string
   * @return the colored string
   */
  public static String colorize(String input) {
    if (input == null || input.trim().isEmpty()) {
      return input;
    }
    return ChatColor.translateAlternateColorCodes('&', input);
  }

  /**
   * Send message
   *
   * @param sender  the receiver
   * @param message the message
   */
  public static void sendMessage(CommandSender sender, String message) {
    sendMessage(sender, message, true);
  }

  /**
   * Send message with prefix
   *
   * @param sender  the receiver
   * @param message the message
   * @param prefix  whether the prefix should be included
   */
  public static void sendMessage(CommandSender sender, String message, boolean prefix) {
    if (prefix) {
      message = MessageConfig.PREFIX.getValue() + message;
    }
    sender.sendMessage(colorize(message));
  }

  /**
   * Create a list of string
   *
   * @param value the object
   * @param trim  should we trim the strings
   * @return the string list
   */
  public static List<String> createStringListFromObject(Object value, boolean trim) {
    List<String> list = new ArrayList<>();
    if (value instanceof Collection) {
      ((Collection<?>) value).forEach(o -> list.add(String.valueOf(o)));
    } else {
      list.add(String.valueOf(value));
    }
    if (trim) {
      list.replaceAll(String::trim);
    }
    return list;
  }
}
