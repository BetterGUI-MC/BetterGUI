package me.hsgamer.bettergui.modifier;

import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.hscore.bukkit.item.ItemModifier;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class ItemFlagModifier implements ItemModifier {
  private List<String> flagList = Collections.emptyList();

  @Override
  public String getName() {
    return "flag";
  }

  private Set<ItemFlag> getParsed(UUID uuid, Collection<StringReplacer> stringReplacers) {
    Set<ItemFlag> flags = new HashSet<>();
    flagList.forEach(string -> {
      string = StringReplacer.replace(string, uuid, stringReplacers);
      try {
        flags.add(ItemFlag.valueOf(string.trim().toUpperCase().replace(" ", "_")));
      } catch (IllegalArgumentException e) {
        MessageUtils.sendMessage(uuid, MessageConfig.INVALID_FLAG.getValue().replace("{input}", string));
      }
    });
    return flags;
  }

  @Override
  public ItemStack modify(ItemStack original, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    ItemMeta itemMeta = original.getItemMeta();
    for (ItemFlag flag : getParsed(uuid, stringReplacerMap.values())) {
      itemMeta.addItemFlags(flag);
    }
    original.setItemMeta(itemMeta);
    return original;
  }

  @Override
  public Object toObject() {
    return flagList;
  }

  @Override
  public void loadFromObject(Object object) {
    this.flagList = CollectionUtils.createStringListFromObject(object, true);
  }

  @Override
  public boolean canLoadFromItemStack(ItemStack itemStack) {
    return itemStack.hasItemMeta();
  }

  @Override
  public void loadFromItemStack(ItemStack itemStack) {
    this.flagList = itemStack.getItemMeta().getItemFlags().stream().map(ItemFlag::name).collect(Collectors.toList());
  }

  @Override
  public boolean compareWithItemStack(ItemStack itemStack, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    Set<ItemFlag> list1 = getParsed(uuid, stringReplacerMap.values());
    if (list1.isEmpty() && (!itemStack.hasItemMeta() || itemStack.getItemMeta().getItemFlags().isEmpty())) {
      return true;
    }
    Set<ItemFlag> list2 = itemStack.getItemMeta().getItemFlags();
    return list1.size() == list2.size() && list1.containsAll(list2);
  }
}
