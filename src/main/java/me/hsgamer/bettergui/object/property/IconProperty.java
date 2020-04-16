package me.hsgamer.bettergui.object.property;

import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Property;
import org.bukkit.entity.Player;

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

  /**
   * Get the parsed string (after replacing the variables)
   *
   * @param input  the string
   * @param player the player involved in
   * @return the parsed string
   */
  protected final String parseFromString(String input, Player player) {
    return icon.hasVariables(player, input) ? icon.setVariables(input, player) : input;
  }
}
