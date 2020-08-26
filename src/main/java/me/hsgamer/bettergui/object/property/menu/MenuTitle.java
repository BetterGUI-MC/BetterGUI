package me.hsgamer.bettergui.object.property.menu;

import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.property.MenuProperty;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.entity.Player;

public class MenuTitle extends MenuProperty<String, String> {

  public MenuTitle(Menu<?> menu) {
    super(menu);
  }

  @Override
  public String getParsed(Player player) {
    return MessageUtils.colorize(parseFromString(getValue(), player));
  }
}
