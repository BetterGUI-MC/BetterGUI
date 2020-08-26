package me.hsgamer.bettergui.object.property.item.impl;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NBT extends ItemProperty<String, String> {

  public NBT(Icon icon) {
    super(icon);
  }

  @Override
  public String getParsed(Player player) {
    return parseFromString(getValue(), player);
  }

  @Override
  public ItemStack parse(Player player, ItemStack itemStack) {
    NBTItem nbtItem = new NBTItem(itemStack);
    nbtItem.mergeCompound(new NBTContainer(getParsed(player)));
    return nbtItem.getItem();
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack itemStack) {
    throw new UnsupportedOperationException("No support comparing with the new method");
  }
}
