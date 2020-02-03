package me.hsgamer.bettergui.object;

import java.util.function.Consumer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This is a combination of ItemStack and InventoryClickEvent
 */
public class ClickableItem {

  private final ItemStack item;
  private final Consumer<InventoryClickEvent> clickEvent;

  public ClickableItem(ItemStack item, Consumer<InventoryClickEvent> clickEvent) {
    this.item = item;
    this.clickEvent = clickEvent;
  }

  /**
   * Get the item
   *
   * @return the item
   */
  public ItemStack getItem() {
    return item;
  }

  /**
   * Get the event
   *
   * @return the event
   */
  public Consumer<InventoryClickEvent> getClickEvent() {
    return clickEvent;
  }
}
