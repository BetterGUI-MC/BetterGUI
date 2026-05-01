package me.hsgamer.bettergui.api.menu;

import me.hsgamer.hscore.variable.VariableManager;

/**
 * The element of the menu
 */
public interface MenuElement {
  /**
   * Get the parent element of this element
   *
   * @return the parent element
   */
  // TODO: Implement
  default MenuElement getParent() {
    return null;
  }

  /**
   * Get the name of the element
   *
   * @return the name
   */
  // TODO: Implement
  default String getName() {
    return null;
  }

  /**
   * Get the variable manager of the element
   *
   * @return the variable manager
   */
  // TODO: Implement
  default VariableManager getVariableManager() {
    return null;
  }

  /**
   * Get the menu containing the element
   *
   * @return the menu
   */
  Menu getMenu();
}
