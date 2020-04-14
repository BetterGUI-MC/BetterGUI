package me.hsgamer.bettergui.object;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.hsgamer.bettergui.manager.VariableManager;
import org.bukkit.entity.Player;

public interface LocalVariableManager<T> {

  /**
   * @return the object involved in this manager
   */
  T getParent();

  /**
   * Register new icon-only variable
   *
   * @param identifier the variable
   * @param variable   the IconVariable object
   */
  void registerVariable(String identifier, LocalVariable variable);

  /**
   * Check if the string contains variables
   *
   * @param player  the player
   * @param message the string
   * @return true if it has, otherwise false
   */
  default boolean hasVariables(Player player, String message) {
    return hasVariables(player, message, true);
  }

  /**
   * Check if the string contains variables
   *
   * @param player      the player
   * @param message     the string
   * @param checkParent whether it checks the the parent manager
   * @return true if it has, otherwise false
   */
  boolean hasVariables(Player player, String message, boolean checkParent);

  /**
   * Replace the variables of the string
   *
   * @param message  the string
   * @param executor the player involved in
   * @return the replaced string
   */
  default String setVariables(String message, Player executor) {
    String old;
    do {
      old = message;
      message = setSingleVariables(message, executor);
    } while (hasVariables(executor, message) && !old.equals(message));
    return VariableManager.setVariables(message, executor);
  }

  /**
   * Replace the variables of the string (single time)
   *
   * @param message  the string
   * @param executor the player involved in
   * @return the replaced string
   */
  default String setSingleVariables(String message, Player executor) {
    return setSingleVariables(message, executor, true);
  }

  /**
   * Replace the variables of the string (single time)
   *
   * @param message     the string
   * @param executor    the player involved in
   * @param checkParent whether it checks the the parent manager
   * @return the replaced string
   */
  String setSingleVariables(String message, Player executor, boolean checkParent);

  /**
   * Replace the local variables of the string
   *
   * @param message   the string
   * @param executor  the player involved in
   * @param pattern   the detect pattern
   * @param variables the map of variables
   * @return the replaced string
   */
  default String setLocalVariables(String message, Player executor, Pattern pattern,
      Map<String, LocalVariable> variables) {
    Matcher matcher = pattern.matcher(message);
    while (matcher.find()) {
      String identifier = matcher.group(1).trim();
      for (Map.Entry<String, LocalVariable> variable : variables.entrySet()) {
        if (identifier.startsWith(variable.getKey())) {
          String replace = variable.getValue()
              .getReplacement(executor, identifier.substring(variable.getKey().length()));
          if (replace != null) {
            message = message
                .replaceAll(Pattern.quote(matcher.group()), Matcher.quoteReplacement(replace));
          }
        }
      }
    }
    return message;
  }
}
