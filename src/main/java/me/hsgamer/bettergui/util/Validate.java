package me.hsgamer.bettergui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

public class Validate {

  private Validate() {

  }

  public static boolean isValidPositiveInteger(String input) {
    try {
      return Integer.parseInt(input) > 0;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  public static boolean isValidInteger(String input) {
    try {
      Integer.parseInt(input);
      return true;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  public static boolean isValidShort(String input) {
    try {
      Short.parseShort(input);
      return true;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  public static boolean isClassLoaded(String className) {
    try {
      Class.forName(className);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  public static boolean isMatch(String string, Pattern detectPattern,
      Collection<String> matchString) {
    Pattern pattern = Pattern.compile("(" + String.join("|", matchString) + ").*");
    Matcher matcher = detectPattern.matcher(string);
    while (matcher.find()) {
      String identifier = matcher.group(1).trim();
      if (pattern.matcher(identifier).find()) {
        return true;
      }
    }
    return false;
  }

  public static List<String> getMissingDepends(List<String> depends) {
    List<String> list = new ArrayList<>();
    for (String depend : depends) {
      if (!Bukkit.getPluginManager().isPluginEnabled(depend)) {
        list.add(depend);
      }
    }
    return list;
  }

  public static boolean isNullOrEmpty(Collection<?> list) {
    return list == null || list.isEmpty();
  }
}