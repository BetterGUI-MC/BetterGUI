package me.hsgamer.bettergui.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

public class Validate {

  private Validate() {

  }

  public static Optional<BigDecimal> getNumber(String input) {
    try {
      return Optional.of(new BigDecimal(input));
    } catch (NumberFormatException ex) {
      return Optional.empty();
    }
  }

  public static boolean isValidPositiveNumber(String input) {
    Optional<BigDecimal> number = getNumber(input);
    return number.filter(bigDecimal -> bigDecimal.compareTo(BigDecimal.ZERO) > 0).isPresent();
  }

  @Deprecated
  public static boolean isValidPositiveInteger(String input) {
    return isValidPositiveNumber(input);
  }

  public static boolean isValidInteger(String input) {
    return getNumber(input).isPresent();
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
      if (Bukkit.getPluginManager().getPlugin(depend) == null) {
        list.add(depend);
      }
    }
    return list;
  }

  public static boolean isNullOrEmpty(Collection<?> list) {
    return list == null || list.isEmpty();
  }

  public static boolean isNullOrEmpty(String string) {
    return string == null || string.isEmpty();
  }
}