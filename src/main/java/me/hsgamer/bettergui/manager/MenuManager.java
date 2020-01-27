package me.hsgamer.bettergui.manager;

import java.util.HashMap;
import java.util.Map;
import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.MenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class MenuManager {

  private Map<String, Menu> menuMap = new HashMap<>();

  public void registerMenu(FileConfiguration file) {
    menuMap.put(file.getName(), MenuBuilder.getMenu(file));
  }

  public void clear() {
    Bukkit.getOnlinePlayers().forEach(player -> {
      InventoryView inventory = player.getOpenInventory();
      if (inventory != null && (inventory.getTopInventory().getHolder() instanceof MenuHolder
          || inventory.getBottomInventory().getHolder() instanceof MenuHolder)) {
        player.closeInventory();
      }
    });
    menuMap.clear();
  }

  public boolean contains(String menu) {
    return menuMap.containsKey(menu);
  }

  public void openMenu(String menu, Player player) {
    menuMap.get(menu).createInventory(player);
  }
}
