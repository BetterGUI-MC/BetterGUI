package me.hsgamer.bettergui.object.inventory;

import fr.mrmicky.fastinv.FastInv;
import java.util.Collection;
import me.hsgamer.bettergui.object.MenuHolder;
import me.hsgamer.bettergui.object.icon.DummyIcon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class DummyInventory extends FastInv implements MenuHolder {

  private Player player;

  public DummyInventory(Player player, int size, Collection<DummyIcon> icons) {
    super(size);
    this.player = player;
    icons.forEach(dummyIcon -> dummyIcon.createClickableItem(player)
        .ifPresent(item -> addItem(item.getItem())));
  }

  public DummyInventory(Player player, int size, String title, Collection<DummyIcon> icons) {
    super(size, title);
    this.player = player;
    icons.forEach(dummyIcon -> dummyIcon.createClickableItem(player)
        .ifPresent(item -> addItem(item.getItem())));
  }

  public DummyInventory(Player player, InventoryType type, Collection<DummyIcon> icons) {
    super(type);
    this.player = player;
    icons.forEach(dummyIcon -> dummyIcon.createClickableItem(player)
        .ifPresent(item -> addItem(item.getItem())));
  }

  public DummyInventory(Player player, InventoryType type, String title,
      Collection<DummyIcon> icons) {
    super(type, title);
    this.player = player;
    icons.forEach(dummyIcon -> dummyIcon.createClickableItem(player)
        .ifPresent(item -> addItem(item.getItem())));
  }

  public void open() {
    open(player);
  }
}
