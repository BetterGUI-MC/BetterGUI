package me.hsgamer.bettergui.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.bettergui.config.PluginConfig;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.MenuHolder;
import me.hsgamer.bettergui.util.BukkitUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class MenuManager {

  private final Map<String, Menu> menuMap = new HashMap<>();

  public void registerMenu(PluginConfig file) {
    String name = file.getFileName();
    FileConfiguration config = file.getConfig();
    if (menuMap.containsKey(name)) {
      BetterGUI.getInstance().getLogger()
          .log(Level.WARNING, "\"{0}\" is already available in the menu manager. Ignored", name);
    } else {
      menuMap.put(name, MenuBuilder.getMenu(name, config));
    }
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

  /**
   * Check if the menu exists
   *
   * @param name the menu name
   * @return true if it exists, otherwise false
   */
  public boolean contains(String name) {
    return menuMap.containsKey(name);
  }

  /**
   * Open the menu for the player
   *
   * @param name   the menu name
   * @param player the player
   * @param bypass whether the plugin ignores the permission check
   */
  public void openMenu(String name, Player player, boolean bypass) {
    menuMap.get(name)
        .createInventory(player, bypass || player.hasPermission(Permissions.OPEN_MENU_BYPASS));
  }

  /**
   * Open the menu for the player
   *
   * @param name       the menu name
   * @param player     the player
   * @param parentMenu the former menu that causes the player to open this menu
   * @param bypass     whether the plugin ignores the permission check
   */
  public void openMenu(String name, Player player, Menu parentMenu, boolean bypass) {
    Menu menu = menuMap.get(name);
    menu.setParentMenu(parentMenu);
    menu.createInventory(player, bypass || player.hasPermission(Permissions.OPEN_MENU_BYPASS));
  }

  /**
   * Get the name of all menus
   *
   * @return the list of the names
   */
  public Set<String> getMenuNames() {
    return menuMap.keySet();
  }
}
