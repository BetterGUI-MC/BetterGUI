package me.hsgamer.bettergui.object.property.item.impl;

import de.tr7zw.nbtapi.NBTItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
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
  public ItemStack parse(Player player, ItemStack itemStack) {
    NBTItem nbtItem = new NBTItem(itemStack);
    nbtItem.setBoolean("Unbreakable", getParsed(player));
    return nbtItem.getItem();
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack itemStack) {
    return (new NBTItem(itemStack)).getBoolean("Unbreakable").equals(getParsed(player));
  }
}
