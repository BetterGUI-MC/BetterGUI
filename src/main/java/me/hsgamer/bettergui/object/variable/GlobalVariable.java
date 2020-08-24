package me.hsgamer.bettergui.object.variable;

import org.bukkit.OfflinePlayer;

/**
 * A variable that is used globally
 */
public interface GlobalVariable {

  /**
   * Get the string from the variable
   *
   * @param executor   the player
   * @param identifier the variable
   * @return the replaced string
   */
  String getReplacement(OfflinePlayer executor, String identifier);
}
