package me.hsgamer.bettergui.api.button;

import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.hscore.bukkit.gui.button.Button;

import java.util.UUID;

/**
 * The wrapped button for Menu
 */
public interface WrappedButton extends Button, MenuElement {
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
