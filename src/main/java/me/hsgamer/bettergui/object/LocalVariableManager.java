package me.hsgamer.bettergui.object;

import me.hsgamer.bettergui.manager.VariableManager;
import org.bukkit.entity.Player;

public interface LocalVariableManager<T> {

  /**
   * Register new icon-only variable
   *
   * @param identifier the variable
   * @param variable   the IconVariable object
   */
  void registerVariable(String identifier, LocalVariable<T> variable);

  /**
   * Check if the string contains variables
   *
   * @param message the string
   * @return true if it has, otherwise false
   */
  boolean hasVariables(String message);

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
    } while (hasVariables(message) && !old.equals(message));
    return VariableManager.setVariables(message, executor);
  }

  /**
   * Replace the variables of the string (single time)
   *
   * @param message  the string
   * @param executor the player involved in
   * @return the replaced string
   */
  String setSingleVariables(String message, Player executor);
}
