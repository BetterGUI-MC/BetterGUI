package me.hsgamer.bettergui.object;

import org.bukkit.entity.Player;

/**
 * A variable that is used globally
 */
public interface GlobalVariable {

  /**
   * Get the string from the variable
   *
   * @param executor the player
   * @param identifier the variable
   * @return the replaced string
   */
  String getReplacement(Player executor, String identifier);
}
