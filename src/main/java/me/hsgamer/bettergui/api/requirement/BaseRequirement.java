package me.hsgamer.bettergui.api.requirement;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.RequirementBuilder;

import java.util.UUID;

/**
 * The base requirement
 *
 * @param <V> the type of the final value
 */
public abstract class BaseRequirement<V> implements Requirement {
  private final String name;
  private final Object value;
  private final Menu menu;

  /**
   * Create a new requirement
   *
   * @param input the input
   */
  protected BaseRequirement(RequirementBuilder.Input input) {
    this.name = input.name;
    this.menu = input.menu;
    this.value = handleValue(input.value);
  }

  /**
   * Get the value from the input value
   *
   * @param inputValue the input value
   *
   * @return the value
   */
  protected Object handleValue(Object inputValue) {
    return inputValue;
  }

  /**
   * Convert the raw value to the final value
   *
   * @param value the raw value
   *
   * @return the final value
   */
  protected abstract V convert(Object value, UUID uuid);

  /**
   * Get the final value
   *
   * @param uuid the unique id
   *
   * @return the final value
   */
  public V getFinalValue(UUID uuid) {
    return convert(value, uuid);
  }

  @Override
  public Menu getMenu() {
    return menu;
  }

  @Override
  public String getName() {
    return name;
  }
}
