package me.hsgamer.bettergui.api.action;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ActionBuilder;

import java.util.UUID;

/**
 * The base action
 */
public abstract class BaseAction implements Action {
  private final Menu menu;
  private final String string;

  /**
   * Create a new action
   *
   * @param input the input
   */
  protected BaseAction(ActionBuilder.Input input) {
    this.menu = input.menu;
    this.string = input.value;
  }

  /**
   * Get the replaced string
   *
   * @param uuid the unique id
   *
   * @return the replaced string
   */
  protected String getReplacedString(UUID uuid) {
    String replaced = menu.replace(string, uuid);
    if (shouldBeTrimmed()) {
      replaced = replaced.trim();
    }
    return replaced;
  }

  /**
   * Check if the string should be trimmed
   *
   * @return true if it should
   */
  protected boolean shouldBeTrimmed() {
    return false;
  }

  @Override
  public Menu getMenu() {
    return menu;
  }
}