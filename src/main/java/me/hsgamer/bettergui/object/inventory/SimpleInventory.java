package me.hsgamer.bettergui.object.inventory;

import fr.mrmicky.fastinv.FastInv;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.MenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SimpleInventory extends FastInv implements MenuHolder {

  private Map<Integer, Icon> icons = new HashMap<>();
  private Icon defaultIcon;
  private long ticks;
  private BukkitTask task;
  private Player player;
  private int maxSlots;

  public SimpleInventory(Player player, int size, String title, Map<Integer, Icon> icons,
      Icon defaultIcon,
      long ticks) {
    super(size, title != null ? title : InventoryType.CHEST.getDefaultTitle());
    this.ticks = ticks;
    this.maxSlots = size;
    this.player = player;
    icons.forEach((key, value) -> this.icons.put(key, value.cloneIcon()));
    if (defaultIcon != null) {
      this.defaultIcon = defaultIcon.cloneIcon();
    }
    createItems();
  }

  public SimpleInventory(Player player, InventoryType type, int maxSlots, String title,
      Map<Integer, Icon> icons,
      Icon defaultIcon, long ticks) {
    super(type, title != null ? title : type.getDefaultTitle());
    this.ticks = ticks;
    this.maxSlots = maxSlots;
    this.player = player;
    icons.forEach((key, value) -> this.icons.put(key - 1, value.cloneIcon()));
    if (defaultIcon != null) {
      this.defaultIcon = defaultIcon.cloneIcon();
    }
    createItems();
  }

  @Override
  public void onOpen(InventoryOpenEvent event) {
    if (ticks > 0) {
      task = new BukkitRunnable() {
        @Override
        public void run() {
          updateItems();
          player.updateInventory();
        }
      }.runTaskTimerAsynchronously(BetterGUI.getInstance(), ticks, ticks);
    }
  }

  @Override
  public void onClose(InventoryCloseEvent event) {
    if (task != null) {
      task.cancel();
    }
  }

  private void createDefaultItem(int slot) {
    if (defaultIcon != null) {
      Optional<ClickableItem> rawDefaultClickableItem = defaultIcon.createClickableItem(player);
      if (rawDefaultClickableItem.isPresent()) {
        ClickableItem clickableItem = rawDefaultClickableItem.get();
        setItem(slot, clickableItem.getItem(), clickableItem.getClickEvent());
      }
    }
  }

  private void updateDefaultItem(int slot) {
    if (defaultIcon != null) {
      Optional<ClickableItem> rawDefaultClickableItem = defaultIcon.updateClickableItem(player);
      if (rawDefaultClickableItem.isPresent()) {
        ClickableItem clickableItem = rawDefaultClickableItem.get();
        setItem(slot, clickableItem.getItem(), clickableItem.getClickEvent());
      }
    }
  }

  private void createItems() {
    for (int i = 0; i < maxSlots; i++) {
      if (icons.containsKey(i)) {
        Optional<ClickableItem> rawClickableItem = icons.get(i).createClickableItem(player);
        if (rawClickableItem.isPresent()) {
          ClickableItem clickableItem = rawClickableItem.get();
          setItem(i, clickableItem.getItem(), clickableItem.getClickEvent());
        } else {
          createDefaultItem(i);
        }
      } else {
        createDefaultItem(i);
      }
    }
  }

  private void updateItems() {
    for (int i = 0; i < maxSlots; i++) {
      if (icons.containsKey(i)) {
        Optional<ClickableItem> rawClickableItem = icons.get(i).updateClickableItem(player);
        if (rawClickableItem.isPresent()) {
          ClickableItem clickableItem = rawClickableItem.get();
          setItem(i, clickableItem.getItem(), clickableItem.getClickEvent());
        } else {
          updateDefaultItem(i);
        }
      } else {
        updateDefaultItem(i);
      }
    }
  }

  public void open() {
    open(player);
  }
}
