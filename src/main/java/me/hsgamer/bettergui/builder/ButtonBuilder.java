package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Optional;

public class ButtonBuilder extends Builder<Menu, WrappedButton> {
  public ButtonBuilder() {
    registerDefaultButtons();
  }

  private void registerDefaultButtons() {
  }

  /**
   * Build the button from the section
   *
   * @param menu    the menu
   * @param name    the name of the button
   * @param section the section
   *
   * @return the button
   */
  public WrappedButton getButton(Menu menu, String name, ConfigurationSection section) {
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section.getValues(true));
    WrappedButton button = Optional.ofNullable(keys.get("type"))
      .map(String::valueOf)
      .flatMap(string -> build(string, menu))
      .orElseGet(() -> build(MainConfig.DEFAULT_BUTTON_TYPE.getValue(), menu).orElse(null));
    if (button != null) {
      button.setName(name);
      button.setFromSection(section);
    }
    return button;
  }
}
