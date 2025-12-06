package me.hsgamer.bettergui.builder;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.bettergui.menu.BaseInventoryMenu;
import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.common.Pair;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * A builder to get the creator to build the {@link Inventory} for any {@link me.hsgamer.bettergui.menu.BaseInventoryMenu} implementation
 */
public class InventoryBuilder extends Builder<Pair<BaseInventoryMenu<?>, Map<String, Object>>, BiFunction<UUID, InventoryHolder, Inventory>> implements Loadable {
  public InventoryBuilder() {
    // EMPTY
  }

  @Override
  public void disable() {
    clear();
  }
}
