package me.hsgamer.bettergui.object.property.item;

import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.IconProperty;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Property for Item
 *
 * @param <V> the final value type from getValue()
 * @param <L> the parsed value type from getParsed()
 */
public abstract class ItemProperty<V, L> extends IconProperty<V> {

  public ItemProperty(Icon icon) {
    super(icon);
  }

  /**
   * Called when getting the parsed value
   *
   * @param player the player involved in
   * @return the parsed value
   */
  public abstract L getParsed(Player player);

  /**
   * Called when parsing value to the item
   *
   * @param player the player involved in
   * @param parent the parent item
   * @return the parsed item
   */
  public abstract ItemStack parse(Player player, ItemStack parent);

  /**
   * Called when comparing a property from the item
   *
   * @param player the player involved in
   * @param item   the item needs comparing
   * @return true if it matches, otherwise false
   */
  public abstract boolean compareWithItemStack(Player player, ItemStack item);
}
