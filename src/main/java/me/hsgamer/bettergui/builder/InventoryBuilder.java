package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIDisplay;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIHolder;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIUtils;
import me.hsgamer.hscore.common.Pair;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * A builder to get the creator to build the {@link Inventory} for any {@link me.hsgamer.bettergui.menu.BaseInventoryMenu} implementation
 */
public class InventoryBuilder extends Builder<Pair<Menu, Map<String, Object>>, BiFunction<BukkitGUIDisplay, UUID, Inventory>> {
  /**
   * The singleton instance
   */
  public static final InventoryBuilder INSTANCE = new InventoryBuilder();

  private InventoryBuilder() {
    register(() -> (display, uuid) -> {
      BukkitGUIHolder holder = display.getHolder();
      InventoryType type = holder.getInventoryType();
      int size = holder.getSize(uuid);
      String title = holder.getTitle(uuid);
      return type == InventoryType.CHEST && size > 0
        ? Bukkit.createInventory(display, BukkitGUIUtils.normalizeToChestSize(size), title)
        : Bukkit.createInventory(display, type, title);
    }, "default");
  }
}
