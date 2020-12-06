package me.hsgamer.bettergui.api.requirement;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.menu.MenuElement;

import java.util.UUID;

/**
 * The requirement
 */
public interface Requirement extends MenuElement {

  /**
   * Called when checking a unique id
   *
   * @param uuid the unique id
   *
   * @return true if the unique id meets the requirement, otherwise false
   */
  boolean check(UUID uuid);

  /**
   * Called when taking the requirements from unique id
   *
   * @param uuid the unique id
   */
  void take(UUID uuid);

  /**
   * Set the value
   *
   * @param value the value
   */
  void setValue(Object value);

  /**
   * Get the name of the requirement
   *
   * @return the name
   */
  String getName();

  /**
   * Set the menu involved in
   *
   * @param menu the menu
   */
  void setMenu(Menu menu);

  /**
   * Whether the requirement is in inverted mode
   *
   * @return true if it is
   */
  boolean isInverted();

  /**
   * Set the inverted mode
   *
   * @param inverted whether it's in inverted mode
   */
  void setInverted(boolean inverted);
}
