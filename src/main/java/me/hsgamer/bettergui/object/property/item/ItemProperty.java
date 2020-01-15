package me.hsgamer.bettergui.object.property.item;

import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.IconProperty;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ItemProperty<V, L> extends IconProperty<V> {

  public ItemProperty(Icon icon) {
    super(icon);
  }

  public abstract L getParsed(Player player);

  public abstract ItemStack parse(Player player, ItemStack parent);

  public abstract boolean compareWithItemStack(Player player, ItemStack item);
}
