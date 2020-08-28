package me.hsgamer.bettergui.object;

/**
 * An abstract class of Property
 *
 * @param <V> The final value type from getValue()
 */
public class Property<V> {

  protected V value;

  /**
   * Get the value
   *
   * @return the value
   */
  public V getValue() {
    return value;
  }

  /**
   * Set the value
   *
   * @param value the value
   */
  @SuppressWarnings("unchecked")
  public void setValue(Object value) {
    this.value = (V) value;
  }
}
