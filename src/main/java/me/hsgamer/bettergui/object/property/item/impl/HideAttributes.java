package me.hsgamer.bettergui.object.property.item.impl;

import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.bettergui.util.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HideAttributes extends ItemProperty<Boolean, Boolean> {

  public HideAttributes(Icon icon) {
    super(icon);
  }

  @Override
  public Boolean getParsed(Player player) {
    return getValue();
  }

  @Override
  public ItemStack parse(Player player, ItemStack parent) {
    return ItemUtils.hideAttributes(parent);
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack item) {
    return !item.getItemFlags().isEmpty() == getParsed(player);
  }
}
