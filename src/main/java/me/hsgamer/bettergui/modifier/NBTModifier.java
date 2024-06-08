package me.hsgamer.bettergui.modifier;

import com.google.gson.Gson;
import me.hsgamer.hscore.bukkit.utils.VersionUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.minecraft.item.ItemModifier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class NBTModifier implements ItemModifier<ItemStack> {
  private static final Gson GSON = new Gson();
  private static final boolean USE_ITEM_COMPONENT = VersionUtils.isAtLeast(20, 5);
  private String nbtData = "";

  @SuppressWarnings("deprecation")
  @Override
  public @NotNull ItemStack modify(@NotNull ItemStack original, UUID uuid, @NotNull Collection<StringReplacer> stringReplacers) {
    if (Validate.isNullOrEmpty(nbtData)) {
      return original;
    }

    String replacedNbtData = StringReplacer.replace(nbtData, uuid, stringReplacers);

    if (USE_ITEM_COMPONENT) {
      Material material = original.getType();
      NamespacedKey materialKey = material.getKey();
      String materialName = materialKey.toString();
      try {
        return Bukkit.getItemFactory().createItemStack(materialName + replacedNbtData);
      } catch (Throwable throwable) {
        return original;
      }
    } else {
      try {
        return Bukkit.getUnsafe().modifyItemStack(original, replacedNbtData);
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
    if (object instanceof Map) {
      Map<?, ?> map = (Map<?, ?>) object;
      this.nbtData = GSON.toJson(map);
    } else {
      this.nbtData = Objects.toString(object, "");
    }
  }
}
