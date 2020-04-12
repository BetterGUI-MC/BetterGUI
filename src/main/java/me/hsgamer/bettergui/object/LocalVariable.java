package me.hsgamer.bettergui.object;

import java.util.Optional;
import org.bukkit.entity.Player;

/**
 * Same as GlobalVariable but this is local
 */
public interface LocalVariable<T> {

  /**
   * @return a string identifying the variable
   */
  String getIdentifier();

  /**
   * @return the icon involved in
   */
  Optional<T> getInvolved();

  /**
   * Get the string from the variable
   *
   * @param executor   the player
   * @param identifier the variable
   * @return the replaced string
   */
  String getReplacement(Player executor, String identifier);
}
