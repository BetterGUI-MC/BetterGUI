package me.hsgamer.bettergui.modifier;

import io.github.projectunified.craftitem.spigot.core.SpigotItem;
import io.github.projectunified.craftitem.spigot.core.SpigotItemModifier;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.minecraft.item.ItemModifier;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

public class CraftItemModifier implements ItemModifier<ItemStack> {
  private final Function<Object, SpigotItemModifier> modifierFunction;
  private SpigotItemModifier modifier;

  public CraftItemModifier(Function<Object, SpigotItemModifier> modifierFunction) {
    this.modifierFunction = modifierFunction;
  }

  @Override
  public @NotNull ItemStack modify(@NotNull ItemStack original, @Nullable UUID uuid, @NotNull StringReplacer stringReplacer) {
    SpigotItem spigotItem = new SpigotItem(original);
    modifier.modify(spigotItem, s -> stringReplacer.replaceOrOriginal(s, uuid));
    return spigotItem.getItemStack();
  }

  @Override
  public Object toObject() {
    return modifier;
  }

  @Override
  public void loadFromObject(Object object) {
    this.modifier = modifierFunction.apply(object);
  }
}
