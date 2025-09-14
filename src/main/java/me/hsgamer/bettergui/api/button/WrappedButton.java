package me.hsgamer.bettergui.api.button;

import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.common.Element;
import me.hsgamer.bettergui.api.menu.MenuElement;

import java.util.UUID;

/**
 * The wrapped button for Menu
 */
public interface WrappedButton extends Button, MenuElement, Element {
  /**
   * Refresh the button for the unique id
   *
   * @param uuid the unique id
   */
  default void refresh(UUID uuid) {
    // EMPTY
  }

  /**
   * Get the name of the button
   *
   * @return the name
   */
  String getName();
}
