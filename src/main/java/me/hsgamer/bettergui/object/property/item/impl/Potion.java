package me.hsgamer.bettergui.object.property.item.impl;

import com.cryptomorin.xseries.XPotion;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

public class Potion extends ItemProperty<List<String>, List<PotionEffect>> {

  public Potion(Icon icon) {
    super(icon);
  }

  @Override
  public List<PotionEffect> getParsed(Player player) {
    List<PotionEffect> potionEffects = new ArrayList<>();
    for (String s : getValue()) {
      Optional<PotionEffect> optional = Optional
          .ofNullable(XPotion.parsePotionEffectFromString(parseFromString(s, player)));
      optional.ifPresent(potionEffects::add);
    }
    return potionEffects;
  }

  @Override
  public ItemStack parse(Player player, ItemStack itemStack) {
    if (XPotion.canHaveEffects(itemStack.getType())) {
      PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
      getParsed(player).forEach(potionEffect -> potionMeta.addCustomEffect(potionEffect, true));
      itemStack.setItemMeta(potionMeta);
    }
    return itemStack;
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack itemStack) {
    List<PotionEffect> list1 = getParsed(player);
    if (list1.isEmpty() &&
        (!itemStack.hasItemMeta()
            || !XPotion.canHaveEffects(itemStack.getType())
            || !((PotionMeta) itemStack.getItemMeta()).hasCustomEffects())) {
      return true;
    }
    List<PotionEffect> list2 = ((PotionMeta) itemStack.getItemMeta()).getCustomEffects();
    if (list1.size() != list2.size()) {
      return false;
    }
    for (PotionEffect potionEffect : list1) {
      if (!list2.contains(potionEffect)) {
        return false;
      }
    }
    return true;
  }
}
