package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.modifier.*;
import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.bukkit.item.ItemModifier;
import me.hsgamer.hscore.bukkit.item.modifier.*;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The item modifier builder
 */
public class ItemModifierBuilder extends Builder<Object, ItemModifier> {

  /**
   * The instance of the item modifier builder
   */
  public static final ItemModifierBuilder INSTANCE = new ItemModifierBuilder();

  private ItemModifierBuilder() {
    registerDefaultItemModifiers();
  }

  private void registerDefaultItemModifiers() {
    register(NameModifier::new, "name");
    register(LoreModifier::new, "lore");
    register(AmountModifier::new, "amount");
    register(DurabilityModifier::new, "durability", "damage");
    register(XMaterialModifier::new, "material", "id", "mat");
    register(MaterialModifier::new, "raw-material", "raw-id", "raw-mat");
    register(EnchantmentModifier::new, "enchantment", "enchant", "enc");
    register(ItemFlagModifier::new, "flag", "item-flags", "itemflag", "itemflags", "item-flag");
    register(PotionModifier::new, "potion", "effect");
    register(SkullModifier::new, "skull", "head", "skull-owner");
  }

  /**
   * Register the item modifier
   *
   * @param itemModifierSupplier the item modifier factory
   * @param name                 the name of the item modifier
   * @param aliases              the aliases of the item modifier
   */
  public void register(Supplier<ItemModifier> itemModifierSupplier, String name, String... aliases) {
    register(o -> {
      ItemModifier itemModifier = itemModifierSupplier.get();
      itemModifier.loadFromObject(o);
      return itemModifier;
    }, name, aliases);
  }

  /**
   * Build the list of item modifiers
   *
   * @param section the section
   *
   * @return the list of the modifiers
   */
  public List<ItemModifier> getItemModifiers(ConfigurationSection section) {
    return section.getValues(false).entrySet()
      .stream()
      .flatMap(entry -> build(entry.getKey(), entry.getValue()).map(Stream::of).orElse(Stream.empty()))
      .collect(Collectors.toList());
  }
}
