package me.hsgamer.bettergui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class CommonUtils {

  private CommonUtils() {

  }

  public static String colorize(String input) {
    if (input == null || input.trim().isEmpty()) {
      return input;
    }
    return ChatColor.translateAlternateColorCodes('&', input);
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
