package me.hsgamer.bettergui.modifier;

import com.cryptomorin.xseries.XEnchantment;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.hscore.bukkit.item.ItemModifier;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class EnchantmentModifier implements ItemModifier {
  private List<String> enchantmentList = Collections.emptyList();

  @Override
  public String getName() {
    return "enchantment";
  }

  private Map<XEnchantment, Integer> getParsed(UUID uuid, Collection<StringReplacer> stringReplacers) {
    Map<XEnchantment, Integer> enchantments = new EnumMap<>(XEnchantment.class);
    for (String string : enchantmentList) {
      Optional<XEnchantment> enchantment;
      string = StringReplacer.replace(string, uuid, stringReplacers);

      int level = 1;
      if (string.contains(",")) {
        String[] split = string.split(",", 2);
        enchantment = XEnchantment.matchXEnchantment(split[0].trim());
        String rawLevel = split[1].trim();
        Optional<BigDecimal> optional = Validate.getNumber(rawLevel);
        if (optional.isPresent()) {
          level = optional.get().intValue();
        } else {
          MessageUtils.sendMessage(uuid, MessageConfig.INVALID_NUMBER.getValue().replace("{input}", rawLevel));
          continue;
        }
      } else {
        enchantment = XEnchantment.matchXEnchantment(string.trim());
      }
      if (enchantment.isPresent()) {
        enchantments.put(enchantment.get(), level);
      } else {
        MessageUtils.sendMessage(uuid, MessageConfig.INVALID_ENCHANTMENT.getValue().replace("{input}", string));
      }
    }
    return enchantments;
  }

  @Override
  public ItemStack modify(ItemStack original, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    ItemMeta itemMeta = original.getItemMeta();
    Map<XEnchantment, Integer> map = getParsed(uuid, stringReplacerMap.values());
    if (itemMeta instanceof EnchantmentStorageMeta) {
      map.forEach((enchant, level) -> ((EnchantmentStorageMeta) itemMeta).addStoredEnchant(enchant.parseEnchantment(), level, true));
    } else {
      map.forEach((enchantment, level) -> itemMeta.addEnchant(enchantment.parseEnchantment(), level, true));
    }
    original.setItemMeta(itemMeta);
    return original;
  }

  @Override
  public Object toObject() {
    return enchantmentList;
  }

  @Override
  public void loadFromObject(Object object) {
    this.enchantmentList = CollectionUtils.createStringListFromObject(object, true);
  }

  @Override
  public boolean canLoadFromItemStack(ItemStack itemStack) {
    return itemStack.hasItemMeta();
  }

  @Override
  public void loadFromItemStack(ItemStack itemStack) {
    this.enchantmentList = itemStack.getItemMeta().getEnchants().entrySet()
      .stream()
      .map(entry -> XEnchantment.matchXEnchantment(entry.getKey()).name() + ", " + entry.getValue())
      .collect(Collectors.toList());
  }

  @Override
  public boolean compareWithItemStack(ItemStack itemStack, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    Map<XEnchantment, Integer> list1 = getParsed(uuid, stringReplacerMap.values());
    if (list1.isEmpty() && (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasEnchants())) {
      return true;
    }
    Map<XEnchantment, Integer> list2 = new EnumMap<>(XEnchantment.class);
    itemStack.getItemMeta().getEnchants().forEach(((enchantment, integer) -> list2.put(XEnchantment.matchXEnchantment(enchantment), integer)));
    if (list1.size() != list2.size()) {
      return false;
    }
    for (Map.Entry<XEnchantment, Integer> entry : list1.entrySet()) {
      XEnchantment enchantment = entry.getKey();
      int lvl = entry.getValue();
      if (!list2.containsKey(enchantment) || list2.get(enchantment) != lvl) {
        return false;
      }
    }
    return true;
  }
}
