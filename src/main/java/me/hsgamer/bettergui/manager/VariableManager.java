package me.hsgamer.bettergui.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.hsgamer.bettergui.config.impl.MainConfig;
import me.hsgamer.bettergui.hook.PlaceholderAPIHook;
import me.hsgamer.bettergui.object.variable.GlobalVariable;
import org.bukkit.OfflinePlayer;

public final class VariableManager {

  private static final Pattern PATTERN = Pattern.compile("[{]([^{}]+)[}]");
  private static final Map<String, GlobalVariable> variables = new HashMap<>();

  private VariableManager() {

  }

  /**
   * Register new variable
   *
   * @param prefix   the prefix
   * @param variable the Variable object
   */
  public static void register(String prefix, GlobalVariable variable) {
    variables.put(prefix, variable);
  }

  /**
   * Check if a string contains variables
   *
   * @param message the string
   * @return true if it has, otherwise false
   */
  public static boolean hasVariables(String message) {
    if (message == null || message.trim().isEmpty()) {
      return false;
    }
    if (isMatch(message, variables.keySet())) {
      return true;
    }
    return PlaceholderAPIHook.hasValidPlugin() && PlaceholderAPIHook.hasPlaceholders(message);
  }

  /**
   * Replace the variables of the string
   *
   * @param message  the string
   * @param executor the player involved in
   * @return the replaced string
   */
  public static String setVariables(String message, OfflinePlayer executor) {
    String old;
    do {
      old = message;
      message = setSingleVariables(message, executor, variables);
    } while (hasVariables(message) && !old.equals(message));
    if (PlaceholderAPIHook.hasValidPlugin()) {
      message = PlaceholderAPIHook.setPlaceholders(message, executor);
    }
    return message;
  }

  /**
   * Replace the local variables of the string
   *
   * @param message         the string
   * @param executor        the player involved in
   * @param globalVariables the map of variables
   * @return the replaced string
   */
  public static String setSingleVariables(String message, OfflinePlayer executor,
      Map<String, ? extends GlobalVariable> globalVariables) {
    Matcher matcher = PATTERN.matcher(message);
    while (matcher.find()) {
      String identifier = matcher.group(1).trim();
      for (Map.Entry<String, ? extends GlobalVariable> variable : globalVariables.entrySet()) {
        if (identifier.startsWith(variable.getKey())) {
          String replace = variable.getValue()
              .getReplacement(executor, identifier.substring(variable.getKey().length()));
          if (replace == null) {
            continue;
          }

          if (MainConfig.REPLACE_ALL_VARIABLES.getValue().equals(Boolean.TRUE)) {
            message = message
                .replaceAll(Pattern.quote(matcher.group()), Matcher.quoteReplacement(replace));
          } else {
            message = message
                .replaceFirst(Pattern.quote(matcher.group()), Matcher.quoteReplacement(replace));
          }
        }
      }
    }
    return message;
  }

  public static boolean isMatch(String string, Collection<String> matchString) {
    Matcher matcher = PATTERN.matcher(string);
    List<String> found = new ArrayList<>();
    while (matcher.find()) {
      found.add(matcher.group(1).trim());
    }

    if (found.isEmpty()) {
      return false;
    } else {
      return found.stream().parallel().anyMatch(s -> {
        for (String match : matchString) {
          if (s.startsWith(match)) {
            return true;
          }
        }
        return false;
      });
    }
  }
}
