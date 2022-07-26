package me.hsgamer.bettergui.builder;

import me.hsgamer.hscore.builder.MassBuilder;
import me.hsgamer.hscore.bukkit.item.ItemModifier;
import me.hsgamer.hscore.bukkit.item.modifier.*;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The item modifier builder
 */
public class ItemModifierBuilder extends MassBuilder<Map.Entry<String, Object>, ItemModifier> {
  /**
   * The instance of the item modifier builder
   */
  public static ItemModifierBuilder INSTANCE = new ItemModifierBuilder();

  private ItemModifierBuilder() {
    register(NameModifier::new, "name");
    register(LoreModifier::new, "lore");
    register(AmountModifier::new, "amount");
    register(DurabilityModifier::new, "durability", "damage");
    register(MaterialModifier::new, "material", "id", "mat");
    register(EnchantmentModifier::new, "enchantment", "enchant", "enc");
    register(ItemFlagModifier::new, "flag", "item-flags", "itemflag", "itemflags", "item-flag");
    register(SkullModifier::new, "skull", "head", "skull-owner");
    register(NBTModifier::new, "nbt", "nbt-data");
  }

  /**
   * Register a new modifier creator
   *
   * @param creator the creator
   * @param type    the type
   */
  public void register(Supplier<ItemModifier> creator, String... type) {
    register(new Element<Map.Entry<String, Object>, ItemModifier>() {
      @Override
      public boolean canBuild(Map.Entry<String, Object> input) {
        String modifier = input.getKey();
        for (String s : type) {
          if (modifier.equalsIgnoreCase(s)) {
            return true;
          }
        }
        return false;
      }

      @Override
      public ItemModifier build(Map.Entry<String, Object> input) {
        ItemModifier itemModifier = creator.get();
        Object value = input.getValue();
        itemModifier.loadFromObject(value);
        return itemModifier;
      }
    });
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
