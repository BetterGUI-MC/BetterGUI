package me.hsgamer.bettergui.api.menu;

import me.hsgamer.hscore.common.StringReplacer;

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
   * Get the string replacer of the element
   *
   * @return the string replacer
   */
  // TODO: Implement
  default StringReplacer getStringReplacer() {
    return null;
  }

  /**
   * Get the menu containing the element
   *
   * @return the menu
   */
  Menu getMenu();
}
