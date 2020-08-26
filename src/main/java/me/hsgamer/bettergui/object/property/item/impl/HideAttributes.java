package me.hsgamer.bettergui.object.property.item.impl;

import de.tr7zw.nbtapi.NBTItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
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
  public ItemStack parse(Player player, ItemStack itemStack) {
    NBTItem nbtItem = new NBTItem(itemStack);
    if (getParsed(player).equals(Boolean.TRUE)) {
      nbtItem.setInteger("HideFlags", 63);
    }
    return nbtItem.getItem();
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack itemStack) {
    NBTItem nbtItem = new NBTItem(itemStack);
    return getParsed(player).equals(Boolean.TRUE) == (nbtItem.getInteger("HideFlags") == 63);
  }
}
