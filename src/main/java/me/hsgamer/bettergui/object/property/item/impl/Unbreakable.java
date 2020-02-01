package me.hsgamer.bettergui.object.property.item.impl;

import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.bettergui.util.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Unbreakable extends ItemProperty<Boolean, Boolean> {

  public Unbreakable(Icon icon) {
    super(icon);
  }

  @Override
  public Boolean getParsed(Player player) {
    return getValue();
  }

  @Override
  public ItemStack parse(Player player, ItemStack parent) {
    return ItemUtils.setUnbreakable(parent);
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack item) {
    return item.hasItemMeta() && (item.getItemMeta().isUnbreakable() == getParsed(player));
  }
}
