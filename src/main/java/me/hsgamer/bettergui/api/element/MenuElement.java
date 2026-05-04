package me.hsgamer.bettergui.api.element;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.common.StringReplacer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The element of the menu
 */
public interface MenuElement extends StringReplacer {
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

  @Override
  @Nullable
  default String replace(@NotNull String arguments) {
    return null;
  }

  @Override
  @Nullable
  default String replace(@NotNull String arguments, @NotNull UUID uuid) {
    return StringReplacer.super.replace(arguments, uuid);
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
