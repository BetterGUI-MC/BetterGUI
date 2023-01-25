package me.hsgamer.bettergui.api.argument;

import me.hsgamer.bettergui.api.menu.MenuElement;

import java.util.Optional;
import java.util.UUID;

/**
 * The base class for argument processors
 */
public interface ArgumentProcessor extends MenuElement {
  /**
   * Process the arguments
   *
   * @param uuid the UUID of the player
   * @param args the arguments
   *
   * @return the remaining arguments, or empty if the arguments are invalid
   */
  Optional<String[]> process(UUID uuid, String[] args);

  /**
   * Called when the display of the menu is removed
   *
   * @param uuid the UUID of the player
   */
  default void onClear(UUID uuid) {
    // EMPTY
  }

  /**
   * Called when the menu is cleared
   */
  default void onClearAll() {
    // EMPTY
  }
}
