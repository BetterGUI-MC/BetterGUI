package me.hsgamer.bettergui.util;

import java.util.ArrayList;
import java.util.List;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommonUtils {

  private CommonUtils() {

  }

  public static String colorize(String input) {
    if (input == null || input.trim().isEmpty()) {
      return input;
    }
    return ChatColor.translateAlternateColorCodes('&', input);
  }

  public static List<String> colorizeList(List<String> input) {
    if (input == null || input.isEmpty()) {
      return input;
    }
    List<String> colorized = new ArrayList<>();
    input.forEach(string -> colorized.add(colorize(string)));
    return colorized;
  }

  public static void sendMessage(CommandSender sender, String message) {
    sendMessage(sender, message, true);
  }

  public static void sendMessage(CommandSender sender, String message, boolean prefix) {
    if (prefix) {
      message = BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.PREFIX) + message;
    }
    sender.sendMessage(colorize(message));
  }

  @Deprecated
  public static List<String> createStringListFromObject(Object value) {
    return createStringListFromObject(value, true);
  }

  @SuppressWarnings("unchecked")
  public static List<String> createStringListFromObject(Object value, boolean trim) {
    List<String> list = new ArrayList<>();
    if (value instanceof List) {
      list.addAll((List<String>) value);
    } else {
      list.add(String.valueOf(value));
    }
    if (trim) {
      list.replaceAll(String::trim);
    }
    return list;
  }
}
