package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.button.*;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The button builder
 */
public class ButtonBuilder extends Builder<Menu, WrappedButton> {

  /**
   * The instance of the button builder
   */
  public static final ButtonBuilder INSTANCE = new ButtonBuilder();

  private ButtonBuilder() {
    registerDefaultButtons();
  }

  private void registerDefaultButtons() {
    register(MenuButton::new, "simple");
    register(DummyButton::new, "dummy");
    register(EmptyButton::new, "empty", "raw");
    register(WrappedAnimatedButton::new, "animated", "animate", "anim");
    register(WrappedListButton::new, "list");
    register(WrappedAirButton::new, "air");
    register(WrappedPredicateButton::new, "predicate", "requirement");
    register(TemplateButton::new, "template");
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

  /**
   * Get the child buttons from the parent button
   *
   * @param parentButton the parent button
   * @param section      the child section
   *
   * @return the child buttons
   */
  public List<WrappedButton> getChildButtons(WrappedButton parentButton, ConfigurationSection section) {
    return section.getKeys(false)
      .stream()
      .map(key -> getButton(parentButton.getMenu(), parentButton.getName() + "_child_" + key, section.getConfigurationSection(key)))
      .collect(Collectors.toList());
  }
}
