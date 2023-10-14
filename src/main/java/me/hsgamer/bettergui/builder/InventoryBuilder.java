package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIDisplay;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIHolder;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.Optional;
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
    register(pair -> (display, uuid) -> {
      BukkitGUIHolder holder = display.getHolder();
      InventoryType type = holder.getInventoryType();
      int size = holder.getSize();
      String title = Optional.ofNullable(MapUtils.getIfFound(pair.getValue(), "name", "title"))
        .map(String::valueOf)
        .map(s -> StringReplacerApplier.replace(s, uuid, pair.getKey()))
        .orElseGet(type::getDefaultTitle);
      return type == InventoryType.CHEST && size > 0
        ? Bukkit.createInventory(display, BukkitGUIUtils.normalizeToChestSize(size), title)
        : Bukkit.createInventory(display, type, title);
    }, "default");
  }
}
