package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.bukkit.gui.Button;
import me.hsgamer.hscore.bukkit.gui.button.ListButton;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class WrappedListButton extends BaseWrappedButton {
  /**
   * Create a new button
   *
   * @param menu the menu
   */
  public WrappedListButton(Menu menu) {
    super(menu);
  }

  @Override
  protected Button createButton(ConfigurationSection section) {
    ListButton button = new ListButton(new LinkedList<>(ButtonBuilder.INSTANCE.getChildButtons(this, section)));
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section.getValues(false));
    Optional.ofNullable(keys.get("keep-current-index")).map(String::valueOf).map(Boolean::parseBoolean).ifPresent(button::setKeepCurrentIndex);
    return button;
  }
}
