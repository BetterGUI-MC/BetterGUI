package me.hsgamer.bettergui.object.property.item.impl;

import com.cryptomorin.xseries.SkullUtils;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Skull extends ItemProperty<String, String> {

  public Skull(Icon icon) {
    super(icon);
  }

  @Override
  public String getParsed(Player player) {
    return parseFromString(getValue(), player);
  }

  @Override
  public ItemStack parse(Player player, ItemStack parent) {
    parent.setItemMeta(SkullUtils.applySkin(parent.getItemMeta(), getParsed(player)));
    return parent;
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack item) {
    throw new UnsupportedOperationException("Cannot compare using the new method");
  }
}
