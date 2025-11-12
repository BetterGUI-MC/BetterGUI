package me.hsgamer.bettergui.modifier;

import me.hsgamer.bettergui.util.SNBTConverter;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.utils.VersionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.minecraft.item.ItemModifier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class NBTModifier implements ItemModifier<ItemStack> {
  private static final boolean USE_ITEM_COMPONENT = VersionUtils.isAtLeast(20, 5);
  private Object nbtData;

  @SuppressWarnings("deprecation")
  @Override
  public @NotNull ItemStack modify(@NotNull ItemStack original, UUID uuid, @NotNull StringReplacer stringReplacer) {
    if (nbtData == null) {
      return original;
    }

    Object replacedNbtData = StringReplacerApplier.replace(nbtData, string -> stringReplacer.replaceOrOriginal(string, uuid));
    String nbtDataString;
    if (replacedNbtData instanceof String) {
      nbtDataString = (String) replacedNbtData;
    } else {
      nbtDataString = MapUtils.castOptionalStringObjectMap(replacedNbtData)
        .map(map -> SNBTConverter.toSNBT(map, USE_ITEM_COMPONENT))
        .orElse(null);
      if (nbtDataString == null) {
        return original;
      }
    }

    if (USE_ITEM_COMPONENT) {
      Material material = original.getType();
      NamespacedKey materialKey = material.getKey();
      String materialName = materialKey.toString();
      try {
        return Bukkit.getItemFactory().createItemStack(materialName + nbtDataString);
      } catch (Throwable throwable) {
        return original;
      }
    } else {
      try {
        return Bukkit.getUnsafe().modifyItemStack(original, nbtDataString);
      } catch (Throwable throwable) {
        return original;
      }
    }
  }

  @Override
  public Object toObject() {
    return nbtData;
  }

  @Override
  public void loadFromObject(Object object) {
    this.nbtData = object;
  }
}
