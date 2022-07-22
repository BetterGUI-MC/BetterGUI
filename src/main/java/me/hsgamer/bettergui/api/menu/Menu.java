package me.hsgamer.bettergui.api.menu;

import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * The menu
 */
public abstract class Menu {

  private final String name;
  private final Map<UUID, Menu> parentMenu = new HashMap<>();

  /**
   * Create a new menu
   *
   * @param name the name of the menu
   */
  protected Menu(String name) {
    this.name = name;
  }

  /**
   * Get the name
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Called when setting options
   *
   * @param config the config of the menu
   */
  public abstract void setFromConfig(Config config);

  /**
   * Called when opening the menu for the player
   *
   * @param player the player involved in
   * @param args   the arguments from the open command
   * @param bypass whether the plugin ignores the permission check
   *
   * @return Whether it's successful
   */
  public abstract boolean create(Player player, String[] args, boolean bypass);

  /**
   * Called when updating the menu
   *
   * @param player the player involved in
   */
  public abstract void update(Player player);

  /**
   * Close the menu
   *
   * @param player the player involved in
   */
  public abstract void close(Player player);

  /**
   * Close/Clear all inventories of the type
   */
  public abstract void closeAll();

  /**
   * Get the original
   *
   * @return the original
   */
  public abstract Object getOriginal();

  /**
   * Get the former menu that opened this menu
   *
   * @param uuid the unique id
   *
   * @return the former menu
   */
  public Optional<Menu> getParentMenu(UUID uuid) {
    return Optional.ofNullable(parentMenu.get(uuid));
  }

  /**
   * Set the former menu
   *
   * @param uuid the unique id
   * @param menu the former menu
   */
  public void setParentMenu(UUID uuid, Menu menu) {
    parentMenu.put(uuid, menu);
  }
}
