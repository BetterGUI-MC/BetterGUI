package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.bukkit.gui.Button;
import org.simpleyaml.configuration.ConfigurationSection;

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
  protected Button createButton(ConfigurationSection section) {
    return Button.EMPTY;
  }
}
