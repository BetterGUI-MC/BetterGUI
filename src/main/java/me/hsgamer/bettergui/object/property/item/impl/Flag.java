package me.hsgamer.bettergui.object.property.item.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Flag extends ItemProperty<List<String>, Set<ItemFlag>> {

  public Flag(Icon icon) {
    super(icon);
  }

  @Override
  public Set<ItemFlag> getParsed(Player player) {
    Set<ItemFlag> flags = new HashSet<>();
    getValue().forEach(s -> {
      try {
        flags.add(ItemFlag.valueOf(s.trim().toUpperCase().replace(" ", "_")));
      } catch (IllegalArgumentException e) {
        CommonUtils.sendMessage(player, BetterGUI.getInstance().getMessageConfig().get(
            DefaultMessage.INVALID_FLAG).replace("{input}", s));
      }
    });
    return flags;
  }

  @Override
  public ItemStack parse(Player player, ItemStack parent) {
    ItemMeta itemMeta = parent.getItemMeta();
    for (ItemFlag flag : getParsed(player)) {
      itemMeta.addItemFlags(flag);
    }
    parent.setItemMeta(itemMeta);
    return parent;
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack item) {
    Set<ItemFlag> list1 = getParsed(player);
    if (list1.isEmpty() && (!item.hasItemMeta() || item.getItemMeta().getItemFlags().isEmpty())) {
      return true;
    }
    Set<ItemFlag> list2 = item.getItemMeta().getItemFlags();
    return list1.size() == list2.size() && list1.containsAll(list2);
  }
}
