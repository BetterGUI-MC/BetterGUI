package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.modifier.NBTModifier;
import me.hsgamer.bettergui.modifier.SkullModifier;
import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.*;
import me.hsgamer.hscore.minecraft.item.ItemModifier;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The item modifier builder
 */
public class ItemModifierBuilder extends FunctionalMassBuilder<Map.Entry<String, Object>, ItemModifier<ItemStack>> {
  /**
   * The instance of the item modifier builder
   */
  public static final ItemModifierBuilder INSTANCE = new ItemModifierBuilder();

  private ItemModifierBuilder() {
    register(NameModifier::new, "name");
    register(LoreModifier::new, "lore");
    register(AmountModifier::new, "amount");
    register(DurabilityModifier::new, "durability", "damage");
    register(MaterialModifier::new, "material", "id", "mat", "raw-material", "raw-id", "raw-mat");
    register(EnchantmentModifier::new, "enchantment", "enchant", "enc");
    register(ItemFlagModifier::new, "flag", "item-flags", "itemflag", "itemflags", "item-flag");
    register(SkullModifier::new, "skull", "head", "skull-owner");
    register(NBTModifier::new, "nbt", "nbt-data");
    register(PotionEffectModifier::new, "potion-effect", "potion", "effect");
  }

  @Override
  protected String getType(Map.Entry<String, Object> input) {
    return input.getKey();
  }

  /**
   * Register a new modifier creator
   *
   * @param creator the creator
   * @param type    the type
   */
  public void register(Supplier<ItemModifier<ItemStack>> creator, String... type) {
    register(input -> {
      ItemModifier<ItemStack> itemModifier = creator.get();
      Object value = input.getValue();
      itemModifier.loadFromObject(value);
      return itemModifier;
    }, type);
  }

  /**
   * Build all modifiers from a case-insensitive map
   *
   * @param map the map
   *
   * @return the modifiers
   */
  public List<ItemModifier<ItemStack>> build(Map<String, Object> map) {
    return map.entrySet()
      .stream()
      .flatMap(entry -> build(entry).map(Stream::of).orElse(Stream.empty()))
      .collect(Collectors.toList());
  }
}
