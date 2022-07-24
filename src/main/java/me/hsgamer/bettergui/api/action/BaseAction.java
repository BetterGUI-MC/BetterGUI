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
  private final boolean canBeReplaced;

  /**
   * Create a new action
   *
   * @param input the input
   */
  protected BaseAction(ActionBuilder.Input input) {
    this.menu = input.menu;
    this.string = input.name;
    this.canBeReplaced = menu.canBeReplaced(string);
  }

  /**
   * Get the replaced string
   *
   * @param uuid the unique id
   *
   * @return the replaced string
   */
  protected String getReplacedString(UUID uuid) {
    return canBeReplaced ? menu.replace(string, uuid) : string;
  }

  @Override
  public Menu getMenu() {
    return menu;
  }
}