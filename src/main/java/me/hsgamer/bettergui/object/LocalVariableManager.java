package me.hsgamer.bettergui.object;

import java.util.Map;
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
    if (message == null || message.trim().isEmpty()) {
      return false;
    }
    if (VariableManager.hasVariables(message)) {
      return true;
    }
    return hasLocalVariables(player, message, true);
  }

  /**
   * Check if the string contains local variables
   *
   * @param player      the player
   * @param message     the string
   * @param checkParent whether it checks the the parent manager
   * @return true if it has, otherwise false
   */
  boolean hasLocalVariables(Player player, String message, boolean checkParent);

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
   * @param variables the map of variables
   * @return the replaced string
   */
  default String setLocalVariables(String message, Player executor,
      Map<String, LocalVariable> variables) {
    return VariableManager.setSingleVariables(message, executor, variables);
  }
}
