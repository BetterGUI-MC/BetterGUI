package me.hsgamer.bettergui.object;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class Menu {

  private String name;

  public Menu(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public abstract void setFromFile(FileConfiguration file);

  public abstract void createInventory(Player player);
}
