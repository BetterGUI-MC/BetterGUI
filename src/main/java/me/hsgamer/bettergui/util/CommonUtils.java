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
    sender.sendMessage(
        colorize(BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.PREFIX) + message));
  }
}
