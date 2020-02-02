package me.hsgamer.bettergui.object;

import java.util.function.Consumer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ClickableItem {

  private final ItemStack item;
  private final Consumer<InventoryClickEvent> clickEvent;

  public ClickableItem(ItemStack item, Consumer<InventoryClickEvent> clickEvent) {
    this.item = item;
    this.clickEvent = clickEvent;
  }

  public ItemStack getItem() {
    return item;
  }

  public Consumer<InventoryClickEvent> getClickEvent() {
    return clickEvent;
  }
}
