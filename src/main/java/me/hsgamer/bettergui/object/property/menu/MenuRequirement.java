package me.hsgamer.bettergui.object.property.menu;

import me.hsgamer.bettergui.object.GlobalRequirement;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.property.MenuProperty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class MenuRequirement extends MenuProperty<ConfigurationSection, GlobalRequirement> {

  public MenuRequirement(Menu<?> menu) {
    super(menu);
  }

  @Override
  public GlobalRequirement getParsed(Player player) {
    return new GlobalRequirement(getValue());
  }
}
