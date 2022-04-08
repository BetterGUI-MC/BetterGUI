package me.hsgamer.bettergui.modifier;

import com.cryptomorin.xseries.XEnchantment;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.hscore.bukkit.item.ItemMetaModifier;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class EnchantmentModifier extends ItemMetaModifier {
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
  public ItemMeta modifyMeta(ItemMeta meta, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    Map<XEnchantment, Integer> map = getParsed(uuid, stringReplacerMap.values());
    Map<Enchantment, Integer> enchantments = new HashMap<>();
    for (Map.Entry<XEnchantment, Integer> entry : map.entrySet()) {
      Enchantment enchantment = entry.getKey().getEnchant();
      if (enchantment != null) {
        enchantments.put(enchantment, entry.getValue());
      }
    }
    if (meta instanceof EnchantmentStorageMeta) {
      enchantments.forEach((enchant, level) -> ((EnchantmentStorageMeta) meta).addStoredEnchant(enchant, level, true));
    } else {
      enchantments.forEach((enchant, level) -> meta.addEnchant(enchant, level, true));
    }
    return meta;
  }

  @Override
  public void loadFromItemMeta(ItemMeta meta) {
    this.enchantmentList = meta.getEnchants().entrySet()
      .stream()
      .map(entry -> XEnchantment.matchXEnchantment(entry.getKey()).name() + ", " + entry.getValue())
      .collect(Collectors.toList());
  }

  @Override
  public boolean canLoadFromItemMeta(ItemMeta meta) {
    return meta.hasEnchants();
  }

  @Override
  public boolean compareWithItemMeta(ItemMeta meta, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    Map<XEnchantment, Integer> list1 = getParsed(uuid, stringReplacerMap.values());
    Map<XEnchantment, Integer> list2 = new EnumMap<>(XEnchantment.class);
    meta.getEnchants().forEach(((enchantment, integer) -> list2.put(XEnchantment.matchXEnchantment(enchantment), integer)));
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

  @Override
  public Object toObject() {
    return enchantmentList;
  }

  @Override
  public void loadFromObject(Object object) {
    this.enchantmentList = CollectionUtils.createStringListFromObject(object, true);
  }
}
