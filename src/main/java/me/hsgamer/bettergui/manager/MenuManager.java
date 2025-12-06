package me.hsgamer.bettergui.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import io.github.projectunified.minelib.plugin.postenable.PostEnable;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ConfigBuilder;
import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.bettergui.config.MainConfig;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

/**
 * The Menu Manager
 */
public final class MenuManager implements Loadable, PostEnable {

  private final Map<String, Menu> menuMap = new HashMap<>();
  private final BetterGUI plugin;
  private final File menusFolder;

  public MenuManager(BetterGUI plugin) {
    this.plugin = plugin;
    this.menusFolder = new File(plugin.getDataFolder(), "menu");
  }

  /**
   * Load the menu config
   */
  public void loadMenuConfig() {
    if (!menusFolder.exists() && menusFolder.mkdirs()) {
      plugin.saveResource("menu/example.yml", false);
      plugin.saveResource("menu/addondownloader.yml", false);
    }
    LinkedList<File> files = new LinkedList<>();
    files.add(menusFolder);
    while (!files.isEmpty()) {
      File file = files.pop();
      if (file.isDirectory()) {
        files.addAll(Arrays.asList(Objects.requireNonNull(file.listFiles())));
      } else if (file.isFile()) {
        this.registerMenu(file);
      }
    }
  }

  /**
   * Register the menu
   *
   * @param file the menu file
   */
  public void registerMenu(File file) {
    String name = plugin.get(MainConfig.class).getFileName(menusFolder, file);
    if (menuMap.containsKey(name)) {
      plugin.getLogger().log(Level.WARNING, "\"{0}\" is already available in the menu manager. Ignored", name);
    } else {
      plugin.get(ConfigBuilder.class).build(file).flatMap(config -> {
        config.setup();
        return plugin.get(MenuBuilder.class).build(config);
      }).ifPresent(menu -> menuMap.put(name, menu));
    }
  }

  /**
   * Clear all menus
   */
  public void clear() {
    menuMap.values().forEach(Menu::closeAll);
    menuMap.clear();
  }

  /**
   * Check if the menu exists
   *
   * @param name the menu name
   *
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
    menuMap.get(name).create(player, args, bypass);
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
  public void openMenu(String name, Player player, String[] args, Menu parentMenu, boolean bypass) {
    Menu menu = menuMap.get(name);
    menu.setParentMenu(player.getUniqueId(), parentMenu);
    menu.create(player, args, bypass);
  }

  /**
   * Get the list of the tab complete
   *
   * @param name   the menu name
   * @param player the player
   * @param args   the arguments from the open command
   *
   * @return the list of the tab complete
   */
  public List<String> tabCompleteMenu(String name, Player player, String[] args) {
    return menuMap.get(name).tabComplete(player, args);
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
   *
   * @return the menu
   */
  public Menu getMenu(String name) {
    return menuMap.get(name);
  }

  @Override
  public void postEnable() {
    loadMenuConfig();
  }

  @Override
  public void disable() {
    clear();
  }
}
