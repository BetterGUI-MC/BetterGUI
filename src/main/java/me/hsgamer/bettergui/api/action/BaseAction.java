package me.hsgamer.bettergui.api.action;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.variable.VariableManager;

import java.util.UUID;

/**
 * The base action
 */
public abstract class BaseAction implements Action {

  private final boolean hasVariables;
  private final String string;
  private Menu menu;

  /**
   * Create a new action
   *
   * @param string the action string
   */
  public BaseAction(String string) {
    this.string = string;
    this.hasVariables = VariableManager.hasVariables(string);
  }

  /**
   * Get the replaced string
   *
   * @param uuid the unique id
   *
   * @return the replaced string
   */
  protected String getReplacedString(UUID uuid) {
    return hasVariables ? VariableManager.setVariables(string, uuid) : string;
  }

  @Override
  public Menu getMenu() {
    return menu;
  }

  @Override
  public void setMenu(Menu menu) {
    this.menu = menu;
  }
}
