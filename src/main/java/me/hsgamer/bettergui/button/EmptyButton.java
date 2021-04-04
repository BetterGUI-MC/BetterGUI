package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.bukkit.gui.button.Button;

import java.util.Map;

public class EmptyButton extends BaseWrappedButton {
  /**
   * Create a new button
   *
   * @param menu the menu
   */
  public EmptyButton(Menu menu) {
    super(menu);
  }

  @Override
  protected Button createButton(Map<String, Object> section) {
    return Button.EMPTY;
  }
}
