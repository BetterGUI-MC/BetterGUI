package me.hsgamer.bettergui.api.requirement;

import me.hsgamer.bettergui.api.element.MenuElement;
import me.hsgamer.bettergui.builder.RequirementBuilder;

import java.util.UUID;

/**
 * The base requirement
 *
 * @param <V> the type of the final value
 */
public abstract class BaseRequirement<V> implements Requirement {
  private final Object value;
  private final String type;
  private final MenuElement parent;

  /**
   * Create a new requirement
   *
   * @param input the input
   */
  protected BaseRequirement(RequirementBuilder.Input input) {
    this.type = input.type;
    this.parent = input.parent;
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
   * Check the requirement for the unique id with the converted value
   *
   * @param uuid  the unique id
   * @param value the converted value
   *
   * @return the result
   */
  protected abstract Result checkConverted(UUID uuid, V value);

  /**
   * Get the final value
   *
   * @param uuid the unique id
   *
   * @return the final value
   */
  public final V getFinalValue(UUID uuid) {
    return convert(value, uuid);
  }

  @Override
  public Result check(UUID uuid) {
    return checkConverted(uuid, getFinalValue(uuid));
  }

  @Override
  public MenuElement getParent() {
    return parent;
  }

  @Override
  public String getName() {
    return type;
  }
}
