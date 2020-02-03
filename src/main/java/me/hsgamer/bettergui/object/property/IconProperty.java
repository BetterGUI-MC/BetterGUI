package me.hsgamer.bettergui.object.property;

import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Property;

/**
 * The property for Icon
 *
 * @param <V> the final value type from getValue()
 */
public abstract class IconProperty<V> extends Property<V> {

  private final Icon icon;

  public IconProperty(Icon icon) {
    this.icon = icon;
  }

  public Icon getIcon() {
    return icon;
  }
}
