package me.hsgamer.bettergui.api.action;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;

import java.util.UUID;

/**
 * The base action
 */
public abstract class BaseAction implements Action {
  protected final ActionBuilder.Input input;

  /**
   * Create a new action
   *
   * @param input the input
   */
  protected BaseAction(ActionBuilder.Input input) {
    this.input = input;
  }

  /**
   * Get the replaced string
   *
   * @param uuid the unique id
   *
   * @return the replaced string
   */
  protected String getReplacedString(UUID uuid) {
    return StringReplacerApplier.replace(input.value, uuid, getMenu());
  }

  @Override
  public Menu getMenu() {
    return input.menu;
  }
}