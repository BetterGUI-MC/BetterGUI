package me.hsgamer.bettergui.object;

import org.bukkit.entity.Player;

/**
 * Same as GlobalVariable but this is icon-only
 */
public interface IconVariable {

  /**
   * @return a string identifying the variable
   */
  String getIdentifier();

  /**
   * @return the icon involved in
   */
  Icon getIcon();

  /**
   * Get the string from the variable
   *
   * @param executor the player
   * @param identifier the variable
   * @return the replaced string
   */
  String getReplacement(Player executor, String identifier);
}
