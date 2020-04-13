package me.hsgamer.bettergui.object.property;

import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.Property;
import org.bukkit.entity.Player;

/**
 * The property for Menu
 *
 * @param <V> the final value type from getValue()
 * @param <L> the final value type from getParsed()
 */
public abstract class MenuProperty<V, L> extends Property<V> {

  private final Menu<?> menu;

  public MenuProperty(Menu<?> menu) {
    this.menu = menu;
  }

  public abstract L getParsed(Player player);

  /**
   * Get the parsed string (after replacing the variables)
   *
   * @param input  the string
   * @param player the player involved in
   * @return the parsed string
   */
  protected final String parseFromString(String input, Player player) {
    return menu.hasVariables(input) ? menu.setVariables(input, player) : input;
  }

  public Menu<?> getMenu() {
    return menu;
  }
}
