package me.hsgamer.bettergui.object.property.item.impl;

import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Name extends ItemProperty<String, String> {

  public Name(Icon icon) {
    super(icon);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    super.value = CommonUtils.colorize(getValue());
  }

  @Override
  public String getParsed(Player player) {
    return parseFromString(getValue(), player);
  }

  @Override
  public ItemStack parse(Player player, ItemStack parent) {
    ItemMeta meta = parent.getItemMeta();
    meta.setDisplayName(getParsed(player));
    parent.setItemMeta(meta);
    return parent;
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack item) {
    if (item.hasItemMeta()) {
      ItemMeta itemMeta = item.getItemMeta();
      if (itemMeta.hasDisplayName()) {
        return getParsed(player).equals(itemMeta.getDisplayName());
      }
    }
    return false;
  }
}
