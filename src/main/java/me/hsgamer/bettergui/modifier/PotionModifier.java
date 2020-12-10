package me.hsgamer.bettergui.modifier;

import com.cryptomorin.xseries.XPotion;
import me.hsgamer.hscore.bukkit.item.ItemModifier;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PotionModifier implements ItemModifier {
  private List<String> potionEffectList = Collections.emptyList();

  public List<PotionEffect> getParsed(UUID uuid, Collection<StringReplacer> stringReplacers) {
    return potionEffectList.stream()
      .map(s -> StringReplacer.replace(s, uuid, stringReplacers))
      .flatMap(s -> Optional.ofNullable(XPotion.parsePotionEffectFromString(s)).map(Stream::of).orElse(Stream.empty()))
      .collect(Collectors.toList());
  }

  @Override
  public String getName() {
    return "potion";
  }

  @Override
  public ItemStack modify(ItemStack original, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    if (XPotion.canHaveEffects(original.getType())) {
      PotionMeta potionMeta = (PotionMeta) original.getItemMeta();
      getParsed(uuid, stringReplacerMap.values()).forEach(potionEffect -> potionMeta.addCustomEffect(potionEffect, true));
      original.setItemMeta(potionMeta);
    }
    return original;
  }

  @Override
  public Object toObject() {
    return potionEffectList;
  }

  @Override
  public void loadFromObject(Object object) {
    this.potionEffectList = CollectionUtils.createStringListFromObject(object, true);
  }

  @Override
  public boolean canLoadFromItemStack(ItemStack itemStack) {
    return XPotion.canHaveEffects(itemStack.getType());
  }

  @Override
  public void loadFromItemStack(ItemStack itemStack) {
    this.potionEffectList = ((PotionMeta) itemStack.getItemMeta()).getCustomEffects()
      .stream()
      .map(potionEffect -> XPotion.matchXPotion(potionEffect.getType()).name() + ", " + potionEffect.getDuration() + ", " + potionEffect.getAmplifier())
      .collect(Collectors.toList());
  }

  @Override
  public boolean compareWithItemStack(ItemStack itemStack, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    List<PotionEffect> list1 = getParsed(uuid, stringReplacerMap.values());
    if (list1.isEmpty() && (!itemStack.hasItemMeta() || !XPotion.canHaveEffects(itemStack.getType()) || !((PotionMeta) itemStack.getItemMeta()).hasCustomEffects())) {
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
