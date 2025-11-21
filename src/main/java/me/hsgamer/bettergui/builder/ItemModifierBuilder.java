package me.hsgamer.bettergui.builder;

import io.github.projectunified.craftitem.core.ItemModifier;
import io.github.projectunified.craftitem.modifier.AmountModifier;
import io.github.projectunified.craftitem.modifier.NameModifier;
import io.github.projectunified.craftitem.spigot.modifier.*;
import io.github.projectunified.craftitem.spigot.nbt.NBTModifier;
import io.github.projectunified.craftitem.spigot.skull.SkullModifier;
import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.hscore.bukkit.utils.VersionUtils;
import me.hsgamer.hscore.common.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The item modifier builder
 */
public class ItemModifierBuilder extends FunctionalMassBuilder<Map.Entry<String, Object>, ItemModifier> {
  /**
   * The instance of the item modifier builder
   */
  public static final ItemModifierBuilder INSTANCE = new ItemModifierBuilder();

  private ItemModifierBuilder() {
    register(entry -> new NameModifier(Objects.toString(entry.getValue())), "name");
    register(entry -> new LoreModifier(CollectionUtils.createStringListFromObject(entry)), "lore");
    register(entry -> new AmountModifier(Objects.toString(entry.getValue())), "amount");
    register(entry -> new DurabilityModifier(Objects.toString(entry.getValue())), "durability", "damage");
    register(entry -> new MaterialModifier(CollectionUtils.createStringListFromObject(entry.getValue(), true)), "material", "id", "mat", "raw-material", "raw-id", "raw-mat");
    register(entry -> new EnchantmentModifier(CollectionUtils.createStringListFromObject(entry.getValue(), true)), "enchantment", "enchant", "enc");
    register(entry -> new ItemFlagModifier(CollectionUtils.createStringListFromObject(entry.getValue(), true)), "flag", "item-flags", "itemflag", "itemflags", "item-flag");
    register(entry -> new SkullModifier(Objects.toString(entry.getValue())), "skull", "head", "skull-owner");
    register(entry -> new NBTModifier(entry.getValue(), VersionUtils.isAtLeast(20, 5)), "nbt", "nbt-data");
    register(entry -> new PotionEffectModifier(CollectionUtils.createStringListFromObject(entry.getValue(), true)), "potion-effect", "potion", "effect");
  }

  @Override
  protected String getType(Map.Entry<String, Object> input) {
    return input.getKey();
  }

  /**
   * Build all modifiers from a case-insensitive map
   *
   * @param map the map
   *
   * @return the modifiers
   */
  public List<ItemModifier> build(Map<String, Object> map) {
    return map.entrySet()
      .stream()
      .flatMap(entry -> build(entry).map(Stream::of).orElse(Stream.empty()))
      .collect(Collectors.toList());
  }
}
