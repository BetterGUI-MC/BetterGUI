package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIDisplay;
import me.hsgamer.hscore.common.Pair;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.function.Function;

/**
 * A builder to get the creator to build the {@link Inventory} for any {@link me.hsgamer.bettergui.menu.BaseInventoryMenu} implementation
 */
public class InventoryBuilder extends Builder<Pair<Menu, Map<String, Object>>, Function<BukkitGUIDisplay, Inventory>> {
  /**
   * The singleton instance
   */
  public static final InventoryBuilder INSTANCE = new InventoryBuilder();

  private InventoryBuilder() {
    // EMPTY
  }
}
