package me.hsgamer.bettergui.object.property.item.impl;

import com.cryptomorin.xseries.XEnchantment;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
      int lvl = 1;
      if (string.contains(",")) {
        String[] split = string.split(",");
        enchantment = XEnchantment.matchXEnchantment(split[0].trim());
        String rawlvl = split[1].trim();
        if (Validate.isValidInteger(rawlvl)) {
          lvl = Integer.parseInt(rawlvl);
        } else {
          String error =
              ChatColor.RED + "Error parsing value!" + rawlvl + " is not a valid number";
          player.sendMessage(error);
          BetterGUI.getInstance().getLogger().warning(error);
          continue;
        }
      } else {
        enchantment = XEnchantment.matchXEnchantment(string.trim());
      }
      if (enchantment.isPresent()) {
        enchantments.put(enchantment.get(), lvl);
      } else {
        String error =
            ChatColor.RED + "Error parsing enchantment!" + string + " is not a valid enchantment";
        player.sendMessage(error);
        BetterGUI.getInstance().getLogger().warning(error);
      }
    }
    return enchantments;
  }

  @Override
  public ItemStack parse(Player player, ItemStack parent) {
    ItemMeta itemMeta = parent.getItemMeta();
    getParsed(player).forEach(
        (enchantment, level) -> itemMeta.addEnchant(enchantment.parseEnchantment(), level, true));
    parent.setItemMeta(itemMeta);
    return parent;
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack item) {
    Map<XEnchantment, Integer> list1 = getParsed(player);
    if (list1.isEmpty() && (!item.hasItemMeta() || item.getItemMeta().hasEnchants())) {
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
