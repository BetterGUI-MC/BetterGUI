package me.hsgamer.bettergui.object.property.menu;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.logging.Level;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.property.MenuProperty;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class MenuInventoryType extends MenuProperty<String, InventoryType> {

  public MenuInventoryType(Menu<?> menu) {
    super(menu);
  }

  @Override
  public InventoryType getParsed(Player player) {
    InventoryType inventoryType;
    try {
      inventoryType = InventoryType.valueOf(parseFromString(getValue(), player).toUpperCase());
      switch (inventoryType) {
        case FURNACE:
        case ENDER_CHEST:
        case CHEST:
        case HOPPER:
        case WORKBENCH:
        case DISPENSER:
        case DROPPER:
          break;
        default:
          inventoryType = InventoryType.CHEST;
          getInstance().getLogger().log(Level.WARNING, "The menu \"" + getMenu().getName()
              + "\"'s inventory type is not supported, it will be CHEST by default");
      }
    } catch (IllegalArgumentException e) {
      inventoryType = InventoryType.CHEST;
      getInstance().getLogger().log(Level.WARNING, "The menu \"" + getMenu().getName()
          + "\" contains an illegal inventory type, it will be CHEST by default");
    }
    return inventoryType;
  }
}
