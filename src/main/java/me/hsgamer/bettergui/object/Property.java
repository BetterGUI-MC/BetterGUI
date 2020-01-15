package me.hsgamer.bettergui.object;

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
