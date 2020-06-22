package me.hsgamer.bettergui.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.bettergui.config.PluginConfig;
import me.hsgamer.bettergui.object.Menu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class MenuManager {

  private final Map<String, Menu<?>> menuMap = new HashMap<>();

  /**
   * Get all parent menus from a menu
   *
   * @param menu   the start menu
   * @param player the player
   * @return all parent menus
   */
  public static List<Menu<?>> getAllParentMenu(Menu<?> menu, Player player) {
    List<Menu<?>> list = new ArrayList<>();
    Optional<Menu<?>> optional = menu.getParentMenu(player);
    while (optional.isPresent()) {
      Menu<?> parentMenu = optional.get();
      if (list.contains(parentMenu)) {
        break;
      }
      list.add(parentMenu);
      optional = parentMenu.getParentMenu(player);
    }
    return list;
  }

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
    menuMap.values().forEach(Menu::closeAll);
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
   * @param args   the arguments from the open command
   * @param bypass whether the plugin ignores the permission check
   */
  public void openMenu(String name, Player player, String[] args, boolean bypass) {
    menuMap.get(name)
        .createInventory(player, args,
            bypass || player.hasPermission(Permissions.OPEN_MENU_BYPASS));
  }

  /**
   * Open the menu for the player
   *
   * @param name       the menu name
   * @param player     the player
   * @param args       the arguments from the open command
   * @param parentMenu the former menu that causes the player to open this menu
   * @param bypass     whether the plugin ignores the permission check
   */
  public void openMenu(String name, Player player, String[] args, Menu<?> parentMenu,
      boolean bypass) {
    Menu<?> menu = menuMap.get(name);
    menu.setParentMenu(player, parentMenu);
    menu.createInventory(player, args,
        bypass || player.hasPermission(Permissions.OPEN_MENU_BYPASS));
  }

  /**
   * Get the name of all menus
   *
   * @return the list of the names
   */
  public Collection<String> getMenuNames() {
    return menuMap.keySet();
  }

  /**
   * Get the menu
   *
   * @param name the menu name
   */
  public Menu<?> getMenu(String name) {
    return menuMap.get(name);
  }
}
