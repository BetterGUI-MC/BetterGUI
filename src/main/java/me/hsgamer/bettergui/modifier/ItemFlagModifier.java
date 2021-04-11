package me.hsgamer.bettergui.modifier;

import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.hscore.bukkit.item.ItemMetaModifier;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class ItemFlagModifier extends ItemMetaModifier {
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
  public ItemMeta modifyMeta(ItemMeta meta, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    for (ItemFlag flag : getParsed(uuid, stringReplacerMap.values())) {
      meta.addItemFlags(flag);
    }
    return meta;
  }

  @Override
  public void loadFromItemMeta(ItemMeta meta) {
    this.flagList = meta.getItemFlags().stream().map(ItemFlag::name).collect(Collectors.toList());
  }

  @Override
  public boolean canLoadFromItemMeta(ItemMeta meta) {
    return true;
  }

  @Override
  public boolean compareWithItemMeta(ItemMeta meta, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    Set<ItemFlag> list1 = getParsed(uuid, stringReplacerMap.values());
    Set<ItemFlag> list2 = meta.getItemFlags();
    return list1.size() == list2.size() && list1.containsAll(list2);
  }

  @Override
  public Object toObject() {
    return flagList;
  }

  @Override
  public void loadFromObject(Object object) {
    this.flagList = CollectionUtils.createStringListFromObject(object, true);
  }
}
