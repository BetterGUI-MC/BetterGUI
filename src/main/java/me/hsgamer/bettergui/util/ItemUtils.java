package me.hsgamer.bettergui.util;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.lang.reflect.Method;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
  private static final boolean USE_ITEM_FLAGS_API;
  private static final boolean USE_ITEM_FLAGS_REFLECTION;

  // Reflection stuff
  private static Class<?> nbtTagCompoundClass;
  private static Class<?> nbtTagListClass;
  private static Method asNmsCopyMethod;
  private static Method asCraftMirrorMethod;
  private static Method hasTagMethod;
  private static Method getTagMethod;
  private static Method setTagMethod;
  private static Method setIntMethod;
  private static Method nbtSetMethod;
  private static Method saveNmsItemStackMethod;

  static {
    // Check if we can use the ItemFlags API
    // We can use the new Bukkit API (1.8.3+)
    USE_ITEM_FLAGS_API = Validate.isClassLoaded("org.bukkit.inventory.ItemFlag");

    // Try to get the NMS methods and classes
    boolean success;
    try {
      nbtTagCompoundClass = NMSUtils.getNMSClass("NBTTagCompound");
      nbtTagListClass = NMSUtils.getNMSClass("NBTTagList");
      Class<?> nmsItemstackClass = NMSUtils.getNMSClass("ItemStack");

      asNmsCopyMethod = NMSUtils.getCraftBukkitClass("inventory.CraftItemStack")
          .getMethod("asNMSCopy", ItemStack.class);
      asCraftMirrorMethod = NMSUtils.getCraftBukkitClass("inventory.CraftItemStack")
          .getMethod("asCraftMirror", nmsItemstackClass);

      setIntMethod = nbtTagCompoundClass.getMethod("setInt", String.class, int.class);
      hasTagMethod = nmsItemstackClass.getMethod("hasTag");
      getTagMethod = nmsItemstackClass.getMethod("getTag");
      setTagMethod = nmsItemstackClass.getMethod("setTag", nbtTagCompoundClass);
      saveNmsItemStackMethod = nmsItemstackClass.getMethod("save", nbtTagCompoundClass);

      nbtSetMethod = nbtTagCompoundClass
          .getMethod("set", String.class, NMSUtils.getNMSClass("NBTBase"));

      success = true;
    } catch (Exception e) {
      getInstance().getLogger()
          .log(Level.WARNING, "Could not enable the attribute remover for this version." +
              "Attributes will show up on items.", e);
      success = false;
    }
    USE_ITEM_FLAGS_REFLECTION = success;
  }

  private ItemUtils() {

  }

  public static ItemStack hideAttributes(ItemStack item) {
    if (item == null) {
      return null;
    }

    if (USE_ITEM_FLAGS_API) {
      ItemMeta meta = item.getItemMeta();
      if (Validate.isNullOrEmpty(meta.getItemFlags())) {
        // Add them only if necessary
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
      }
      return item;

    } else if (USE_ITEM_FLAGS_REFLECTION) {
      try {

        Object nmsItemstack = asNmsCopyMethod.invoke(null, item);
        if (nmsItemstack == null) {
          return item;
        }

        Object nbtCompound;
        if ((boolean) hasTagMethod.invoke(nmsItemstack)) {
          nbtCompound = getTagMethod.invoke(nmsItemstack);
        } else {
          nbtCompound = nbtTagCompoundClass.getDeclaredConstructor().newInstance();
          setTagMethod.invoke(nmsItemstack, nbtCompound);
        }

        if (nbtCompound == null) {
          return item;
        }

        Object nbtList = nbtTagListClass.getDeclaredConstructor().newInstance();
        nbtSetMethod.invoke(nbtCompound, "AttributeModifiers", nbtList);
        return (ItemStack) asCraftMirrorMethod.invoke(null, nmsItemstack);

      } catch (Exception t) {
        // Ignore
      }
    }

    // On failure just return the item
    return item;
  }

  public static ItemStack setUnbreakable(ItemStack item) {
    try {
      Object nmsItemstack = asNmsCopyMethod.invoke(null, item);
      if (nmsItemstack == null) {
        return item;
      }

      Object nbtCompound;
      if ((boolean) hasTagMethod.invoke(nmsItemstack)) {
        nbtCompound = getTagMethod.invoke(nmsItemstack);
      } else {
        nbtCompound = nbtTagCompoundClass.getDeclaredConstructor().newInstance();
        setTagMethod.invoke(nmsItemstack, nbtCompound);
      }

      if (nbtCompound == null) {
        return item;
      }

      setIntMethod.invoke(nbtCompound, "Unbreakable", 1);
      setTagMethod.invoke(nmsItemstack, nbtCompound);

      return (ItemStack) asCraftMirrorMethod.invoke(null, nmsItemstack);
    } catch (Exception t) {
      // Ignore
    }

    // On failure just return the item
    return item;
  }

  public static String convertItemStackToJson(ItemStack item) {
    Object itemAsJsonObject;
    try {
      Object nmsNbtTagCompoundObj = nbtTagCompoundClass.getDeclaredConstructor().newInstance();
      Object nmsItemStackObj = asNmsCopyMethod.invoke(null, item);
      itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
    } catch (Exception e) {
      getInstance().getLogger()
          .log(Level.WARNING, "Could not convert ItemStack to JSON", e);
      return null;
    }
    return itemAsJsonObject.toString();
  }
}
