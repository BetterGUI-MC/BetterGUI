package me.hsgamer.bettergui.api.button;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.common.Element;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * The base class of wrapped button
 */
public abstract class MenuButton implements Button, MenuElement, Element {
  protected final Menu menu;
  protected final String name;
  protected final Map<String, Object> options;
  protected Button button;

  /**
   * Create a new wrapped button
   *
   * @param input the input
   */
  protected MenuButton(ButtonBuilder.Input input) {
    this.menu = input.menu;
    this.name = input.name;
    this.options = input.options;
  }

  /**
   * Create the button from the section
   *
   * @param section the section
   *
   * @return the button
   */
  protected abstract Button createButton(Map<String, Object> section);

  /**
   * Refresh the button for the unique id
   *
   * @param uuid the unique id
   */
  public void refresh(UUID uuid) {
    // EMPTY
  }

  /**
   * Get the options of the button
   *
   * @return the options
   */
  public Map<String, Object> getOptions() {
    return options;
  }

  public String getName() {
    return name;
  }

  @Override
  public Menu getMenu() {
    return menu;
  }

  @Override
  public boolean apply(@NotNull UUID uuid, @NotNull ActionItem actionItem) {
    if (button != null) {
      return button.apply(uuid, actionItem);
    }
    return false;
  }

  @Override
  public void init() {
    this.button = createButton(options);
    Element.handleIfElement(this.button, Element::init);
  }

  @Override
  public void stop() {
    Element.handleIfElement(this.button, Element::stop);
  }
}
