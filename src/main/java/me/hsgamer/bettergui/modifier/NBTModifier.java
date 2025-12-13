package me.hsgamer.bettergui.modifier;

import io.github.projectunified.craftitem.nbt.NBTMapNormalizer;
import io.github.projectunified.craftitem.nbt.SNBTConverter;
import me.hsgamer.hscore.bukkit.utils.VersionUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.minecraft.item.ItemModifier;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * This is a legacy modifier that was removed from HSCore.
 * I decide to keep it here for compatibility.
 */
public class NBTModifier implements ItemModifier<ItemStack> {
  private static final boolean USE_ITEM_COMPONENT = VersionUtils.isAtLeast(20, 5);
  private Object value;

  @Override
  public @NotNull ItemStack modify(@NotNull ItemStack original, UUID uuid, @NotNull StringReplacer stringReplacer) {
    String nbtString;
    if (value instanceof Map) {
      Object normalized = NBTMapNormalizer.normalize(value, s -> stringReplacer.replaceOrOriginal(s, uuid));
      nbtString = SNBTConverter.convert(normalized, USE_ITEM_COMPONENT);
    } else {
      nbtString = stringReplacer.replaceOrOriginal(Objects.toString(value), uuid);
    }
    ItemStack appliedItemStack = applyNBT(original, nbtString);
    return appliedItemStack == null ? original : appliedItemStack;
  }

  @SuppressWarnings("deprecation")
  private ItemStack applyNBT(ItemStack item, String nbtString) {
    try {
      if (USE_ITEM_COMPONENT) {
        String materialName = item.getType().getKey().toString();
        return Bukkit.getItemFactory().createItemStack(materialName + nbtString);
      } else {
        return Bukkit.getUnsafe().modifyItemStack(item, nbtString);
      }
    } catch (Throwable ignored) {
    }
    return null;
  }

  @Override
  public Object toObject() {
    return value;
  }

  @Override
  public void loadFromObject(Object object) {
    this.value = object;
  }
}
