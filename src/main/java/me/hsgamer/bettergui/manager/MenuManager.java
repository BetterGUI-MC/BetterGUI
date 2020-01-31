package me.hsgamer.bettergui.manager;

import java.util.HashMap;
import java.util.Map;
import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.bettergui.config.PluginConfig;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.MenuHolder;
import me.hsgamer.bettergui.util.BukkitUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class MenuManager {

  private Map<String, Menu> menuMap = new HashMap<>();

  public void registerMenu(PluginConfig file) {
    menuMap.put(file.getFileName(), MenuBuilder.getMenu(file.getFileName(), file.getConfig()));
  }

  public void clear() {
    BukkitUtils.getOnlinePlayers().forEach(player -> {
      InventoryView inventory = player.getOpenInventory();
      if (inventory != null && (inventory.getTopInventory().getHolder() instanceof MenuHolder
          || inventory.getBottomInventory().getHolder() instanceof MenuHolder)) {
        player.closeInventory();
      }
    });
    menuMap.clear();
  }

  public boolean contains(String name) {
    return menuMap.containsKey(name);
  }

  public void openMenu(String name, Player player) {
    menuMap.get(name).createInventory(player);
  }

  public void openMenu(String name, Player player, Menu parentMenu) {
    Menu menu = menuMap.get(name);
    menu.setParentMenu(parentMenu);
    menu.createInventory(player);
  }
}
