package me.hsgamer.bettergui.object.property.item.impl;

import com.cryptomorin.xseries.XEnchantment;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Enchantment extends ItemProperty<List<String>, Map<XEnchantment, Integer>> {

  public Enchantment(Icon icon) {
    super(icon);
  }

  @Override
  public Map<XEnchantment, Integer> getParsed(Player player) {
    Map<XEnchantment, Integer> enchantments = new EnumMap<>(XEnchantment.class);
    for (String string : getValue()) {
      Optional<XEnchantment> enchantment;
      string = parseFromString(string, player);

      int level = 1;
      if (string.contains(",")) {
        String[] split = string.split(",");
        enchantment = XEnchantment.matchXEnchantment(split[0].trim());
        String rawLevel = split[1].trim();
        Optional<BigDecimal> optional = Validate.getNumber(rawLevel);
        if (optional.isPresent()) {
          level = optional.get().intValue();
        } else {
          MessageUtils.sendMessage(player,
              MessageConfig.INVALID_NUMBER.getValue().replace("{input}", rawLevel));
          continue;
        }
      } else {
        enchantment = XEnchantment.matchXEnchantment(string.trim());
      }
      if (enchantment.isPresent()) {
        enchantments.put(enchantment.get(), level);
      } else {
        MessageUtils.sendMessage(player,
            MessageConfig.INVALID_ENCHANTMENT.getValue().replace("{input}", string));
      }
    }
    return enchantments;
  }

  @Override
  public ItemStack parse(Player player, ItemStack parent) {
    ItemMeta itemMeta = parent.getItemMeta();
    Map<XEnchantment, Integer> map = getParsed(player);
    if (itemMeta instanceof EnchantmentStorageMeta) {
      map.forEach((enchant, level) -> ((EnchantmentStorageMeta) itemMeta)
          .addStoredEnchant(enchant.parseEnchantment(), level, true));
    } else {
      map.forEach(
          (enchantment, level) -> itemMeta.addEnchant(enchantment.parseEnchantment(), level, true));
    }
    parent.setItemMeta(itemMeta);
    return parent;
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack item) {
    Map<XEnchantment, Integer> list1 = getParsed(player);
    if (list1.isEmpty() && (!item.hasItemMeta() || !item.getItemMeta().hasEnchants())) {
      return true;
    }
    Map<XEnchantment, Integer> list2 = new EnumMap<>(XEnchantment.class);
    item.getItemMeta().getEnchants().forEach(((enchantment, integer) -> list2
        .put(XEnchantment.matchXEnchantment(enchantment), integer)));
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
