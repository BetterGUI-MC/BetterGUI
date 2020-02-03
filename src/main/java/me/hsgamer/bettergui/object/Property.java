package me.hsgamer.bettergui.object;

/**
 * An abstract class of Property
 *
 * @param <V> The final value type from getValue()
 */
public abstract class Property<V> {

  protected V value;

  public V getValue() {
    return value;
  }

  @SuppressWarnings("unchecked")
  public void setValue(Object value) {
    this.value = (V) value;
  }
}
