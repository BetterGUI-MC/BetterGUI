package me.hsgamer.bettergui.object.property.item.impl;

import java.util.ArrayList;
import java.util.List;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Lore extends ItemProperty<List<String>, List<String>> {

  public Lore(Icon icon) {
    super(icon);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    super.value = CommonUtils.colorize(getValue());
  }

  @Override
  public List<String> getParsed(Player player) {
    List<String> parsed = new ArrayList<>();
    getValue().forEach(lore -> parsed
        .add(getIcon().hasVariables(lore) ? getIcon().setVariables(lore, player) : lore));
    return parsed;
  }

  @Override
  public ItemStack parse(Player player, ItemStack parent) {
    ItemMeta meta = parent.getItemMeta();
    meta.setLore(getParsed(player));
    parent.setItemMeta(meta);
    return parent;
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack item) {
    if (item.hasItemMeta()) {
      ItemMeta itemMeta = item.getItemMeta();
      if (itemMeta.hasLore()) {
        return getParsed(player).equals(itemMeta.getLore());
      }
    }
    return false;
  }
}
