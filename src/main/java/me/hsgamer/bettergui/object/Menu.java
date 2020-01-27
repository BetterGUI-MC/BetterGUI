package me.hsgamer.bettergui.object;

import java.util.Optional;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class Menu {

  private String name;
  private Menu parentMenu;

  public Menu(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public abstract void setFromFile(FileConfiguration file);

  public abstract void createInventory(Player player);

  public Optional<Menu> getParentMenu() {
    return Optional.ofNullable(parentMenu);
  }

  public void setParentMenu(Menu parentMenu) {
    this.parentMenu = parentMenu;
  }
}
