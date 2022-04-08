package me.hsgamer.bettergui.modifier;

import com.cryptomorin.xseries.XPotion;
import me.hsgamer.hscore.bukkit.item.ItemMetaModifier;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.stream.Collectors;

public class PotionModifier extends ItemMetaModifier {
  private List<String> potionEffectList = Collections.emptyList();

  public List<PotionEffect> getParsed(UUID uuid, Collection<StringReplacer> stringReplacers) {
    List<String> list = new ArrayList<>(potionEffectList);
    list.replaceAll(s -> StringReplacer.replace(s, uuid, stringReplacers));
    return XPotion.parseEffects(list).stream().map(XPotion.Effect::getEffect).collect(Collectors.toList());
  }

  @Override
  public String getName() {
    return "potion";
  }

  @Override
  public ItemMeta modifyMeta(ItemMeta meta, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    if (meta instanceof PotionMeta) {
      PotionMeta potionMeta = (PotionMeta) meta;
      getParsed(uuid, stringReplacerMap.values()).forEach(potionEffect -> potionMeta.addCustomEffect(potionEffect, true));
      return potionMeta;
    }
    return meta;
  }

  @Override
  public void loadFromItemMeta(ItemMeta meta) {
    this.potionEffectList = ((PotionMeta) meta).getCustomEffects()
      .stream()
      .map(potionEffect -> XPotion.matchXPotion(potionEffect.getType()).name() + ", " + potionEffect.getDuration() + ", " + potionEffect.getAmplifier())
      .collect(Collectors.toList());
  }

  @Override
  public boolean canLoadFromItemMeta(ItemMeta meta) {
    return meta instanceof PotionMeta;
  }

  @Override
  public boolean compareWithItemMeta(ItemMeta meta, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    if (!(meta instanceof PotionMeta)) {
      return false;
    }
    List<PotionEffect> list1 = getParsed(uuid, stringReplacerMap.values());
    List<PotionEffect> list2 = ((PotionMeta) meta).getCustomEffects();
    return list1.size() == list2.size() && list1.containsAll(list2);
  }

  @Override
  public Object toObject() {
    return potionEffectList;
  }

  @Override
  public void loadFromObject(Object object) {
    this.potionEffectList = CollectionUtils.createStringListFromObject(object, true);
  }
}
