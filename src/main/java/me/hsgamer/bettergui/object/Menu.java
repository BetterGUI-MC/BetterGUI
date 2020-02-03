package me.hsgamer.bettergui.object;

import java.util.Optional;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class Menu {

  private final String name;
  private Menu parentMenu;

  public Menu(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  /**
   * Called when setting options
   *
   * @param file the file of the menu
   */
  public abstract void setFromFile(FileConfiguration file);

  /**
   * Called when opening the menu for the player
   *
   * @param player the player involved in
   */
  public abstract void createInventory(Player player);

  /**
   * Get the former menu that opened this menu
   *
   * @return the former menu
   */
  public Optional<Menu> getParentMenu() {
    return Optional.ofNullable(parentMenu);
  }

  /**
   * Set the former menu
   *
   * @param parentMenu the former menu
   */
  public void setParentMenu(Menu parentMenu) {
    this.parentMenu = parentMenu;
  }
}
