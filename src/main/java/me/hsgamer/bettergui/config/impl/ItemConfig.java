package me.hsgamer.bettergui.config.impl;

import me.hsgamer.bettergui.config.PluginConfig;
import me.hsgamer.bettergui.object.menu.DummyMenu;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemConfig extends PluginConfig {

  private DummyMenu menu;

  public ItemConfig(JavaPlugin plugin) {
    super(plugin, "items.yml");
  }

  public void initializeMenu() {
    menu = new DummyMenu("dummyitems");
    menu.setFromFile(getConfig());
  }

  public void createMenu(Player player) {
    menu.createInventory(player);
  }

  public DummyMenu getMenu() {
    return menu;
  }
}
