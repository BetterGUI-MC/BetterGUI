package me.hsgamer.bettergui.api.requirement;

import me.hsgamer.bettergui.api.menu.Menu;

import java.util.UUID;

/**
 * The base requirement
 *
 * @param <V> the type of the final value
 */
public abstract class BaseRequirement<V> implements Requirement {

  private final String name;
  protected Object value;
  private Menu menu;

  public BaseRequirement(String name) {
    this.name = name;
  }

  /**
   * Called when getting the final values
   *
   * @param uuid the unique id
   *
   * @return the final value
   */
  public abstract V getParsedValue(UUID uuid);

  @Override
  public Menu getMenu() {
    return menu;
  }

  @Override
  public void setMenu(Menu menu) {
    this.menu = menu;
  }

  @Override
  public void setValue(Object value) {
    this.value = value;
  }

  @Override
  public String getName() {
    return name;
  }
}
