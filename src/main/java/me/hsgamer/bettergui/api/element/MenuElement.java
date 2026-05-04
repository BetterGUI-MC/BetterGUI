package me.hsgamer.bettergui.api.element;

import me.hsgamer.bettergui.api.menu.Menu;
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
  MenuElement getParent();

  /**
   * Get the name of the element
   *
   * @return the name
   */
  String getName();

  /**
   * Get the string replacer of this element
   *
   * @return the string replacer
   */
  default StringReplacer getStringReplacer() {
    return StringReplacer.DUMMY;
  }

  /**
   * Get the menu containing the element
   *
   * @return the menu
   */
  default Menu getMenu() {
    MenuElement parent = getParent();
    if (parent == null) {
      throw new IllegalStateException("This element has no parent! It's likely a Menu itself");
    } else {
      return parent.getMenu();
    }
  }
}
