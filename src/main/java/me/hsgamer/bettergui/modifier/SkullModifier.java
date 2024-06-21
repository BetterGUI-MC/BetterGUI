package me.hsgamer.bettergui.modifier;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.hsgamer.hscore.bukkit.item.modifier.ItemMetaComparator;
import me.hsgamer.hscore.bukkit.item.modifier.ItemMetaModifier;
import me.hsgamer.hscore.bukkit.utils.BukkitUtils;
import me.hsgamer.hscore.bukkit.utils.VersionUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.common.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Base64;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * This is a legacy modifier that was removed from HSCore.
 * I decide to keep it here for compatibility.
 */
@SuppressWarnings("deprecation")
public class SkullModifier implements ItemMetaModifier, ItemMetaComparator {
  /**
   * <a href="https://github.com/CryptoMorin/XSeries/blob/b633d00608435701f1045a566b98a81edd5f923c/src/main/java/com/cryptomorin/xseries/profiles/objects/ProfileInputType.java#L29C35-L29C50">...</a>
   */
  private static final Pattern MOJANG_SHA256_APPROX = Pattern.compile("[0-9a-z]{55,70}");
  private static final SkullMeta delegateSkullMeta;
  private static final SkullHandler skullHandler = new SkullHandler();

  static {
    ItemStack itemStack;
    if (VersionUtils.isAtLeast(13)) {
      itemStack = new ItemStack(Material.valueOf("PLAYER_HEAD"));
    } else {
      itemStack = new ItemStack(Material.valueOf("SKULL_ITEM"));
      itemStack.setDurability((short) 3);
    }
    delegateSkullMeta = (SkullMeta) Objects.requireNonNull(itemStack.getItemMeta());
  }

  private String skullString = "";

  private static void setSkull(SkullMeta meta, String skull) {
    if (BukkitUtils.isUsername(skull)) {
      skullHandler.setSkullByName(meta, skull);
    } else if (Validate.isValidUUID(skull)) {
      skullHandler.setSkullByUUID(meta, UUID.fromString(skull));
    } else if (Validate.isValidURL(skull)) {
      skullHandler.setSkullByURL(meta, skull);
    } else if (MOJANG_SHA256_APPROX.matcher(skull).matches()) {
      skullHandler.setSkullByURL(meta, "https://textures.minecraft.net/texture/" + skull);
    }
  }

  private static SkullMeta getSkullMeta(String skull) {
    SkullMeta meta = delegateSkullMeta.clone();
    setSkull(meta, skull);
    return meta;
  }

  private String getFinalSkullString(UUID uuid, Collection<StringReplacer> replacers) {
    return StringReplacer.replace(skullString, uuid, replacers);
  }

  @Override
  public @NotNull ItemMeta modifyMeta(@NotNull ItemMeta meta, @Nullable UUID uuid, @NotNull Collection<StringReplacer> stringReplacers) {
    if (!(meta instanceof SkullMeta)) {
      return meta;
    }
    setSkull((SkullMeta) meta, getFinalSkullString(uuid, stringReplacers));
    return meta;
  }

  @Override
  public boolean loadFromItemMeta(ItemMeta meta) {
    if (meta instanceof SkullMeta) {
      skullString = skullHandler.getSkullValue((SkullMeta) meta);
      return true;
    }
    return false;
  }

  @Override
  public boolean compare(@NotNull ItemMeta meta, @Nullable UUID uuid, @NotNull Collection<StringReplacer> stringReplacers) {
    if (!(meta instanceof SkullMeta)) {
      return false;
    }
    return skullHandler.compareSkull(
      getSkullMeta(getFinalSkullString(uuid, stringReplacers)),
      (SkullMeta) meta
    );
  }

  @Override
  public Object toObject() {
    return skullString;
  }

  @Override
  public void loadFromObject(Object object) {
    this.skullString = String.valueOf(object);
  }

  private static class SkullHandler {
    private final Method getProfileMethod;

    private SkullHandler() {
      Method method = null;
      try {
        //noinspection JavaReflectionMemberAccess
        method = Property.class.getDeclaredMethod("value");
      } catch (Exception e) {
        try {
          //noinspection JavaReflectionMemberAccess
          method = Property.class.getDeclaredMethod("getValue");
        } catch (NoSuchMethodException ex) {
          // IGNORE
        }
      }
      getProfileMethod = method;
    }

    public void setSkullByName(SkullMeta meta, String name) {
      setSkullByPlayer(meta, Bukkit.getOfflinePlayer(name));
    }

    public void setSkullByUUID(SkullMeta meta, UUID uuid) {
      setSkullByPlayer(meta, Bukkit.getOfflinePlayer(uuid));
    }

    public void setSkullByURL(SkullMeta meta, String url) {
      try {
        setSkullByURL(meta, new URL(url));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @SuppressWarnings("deprecation")
    public void setSkullByPlayer(SkullMeta meta, OfflinePlayer player) {
      if (VersionUtils.isAtLeast(12)) {
        meta.setOwningPlayer(player);
      } else {
        meta.setOwner(player.getName());
      }
    }

    public void setSkullByURL(SkullMeta meta, URL url) {
      GameProfile profile = new GameProfile(UUID.randomUUID(), null);
      profile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes())));

      try {
        Method setProfile = meta.getClass().getMethod("setProfile", GameProfile.class);
        setProfile.setAccessible(true);
        setProfile.invoke(meta, profile);
      } catch (Exception e) {
        try {
          Field profileField = meta.getClass().getDeclaredField("profile");
          profileField.setAccessible(true);
          profileField.set(meta, profile);
        } catch (Exception ignored) {
          // IGNORE
        }
      }
    }

    public String getSkullValue(SkullMeta meta) {
      GameProfile profile;
      try {
        Field profileField = meta.getClass().getDeclaredField("profile");
        profileField.setAccessible(true);
        profile = (GameProfile) profileField.get(meta);
      } catch (Exception e) {
        return "";
      }

      Collection<Property> properties = profile.getProperties().get("textures");
      if (properties == null || properties.isEmpty()) {
        return "";
      }

      for (Property property : properties) {
        String value;
        try {
          value = (String) getProfileMethod.invoke(property);
        } catch (Exception e) {
          continue;
        }

        if (!value.isEmpty()) {
          return value;
        }
      }
      return "";
    }

    public boolean compareSkull(SkullMeta meta1, SkullMeta meta2) {
      return getSkullValue(meta1).equals(getSkullValue(meta2));
    }
  }
}
