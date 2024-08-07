package me.hsgamer.bettergui.modifier;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.hsgamer.hscore.bukkit.item.modifier.ItemMetaModifier;
import me.hsgamer.hscore.bukkit.utils.VersionUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.common.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * This is a legacy modifier that was removed from HSCore.
 * I decide to keep it here for compatibility.
 */
@SuppressWarnings("deprecation")
public class SkullModifier implements ItemMetaModifier {
  /**
   * <a href="https://github.com/CryptoMorin/XSeries/blob/b633d00608435701f1045a566b98a81edd5f923c/src/main/java/com/cryptomorin/xseries/profiles/objects/ProfileInputType.java#L29C35-L29C50">...</a>
   */
  private static final Pattern MOJANG_SHA256_APPROX = Pattern.compile("[0-9a-z]{55,70}");
  private static final SkullMeta delegateSkullMeta;
  private static final SkullHandler skullHandler = getSkullHandler();
  private static final Map<String, GameProfile> cache = new ConcurrentHashMap<>();

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

  private static SkullHandler getSkullHandler() {
    try {
      Class.forName("org.bukkit.profile.PlayerProfile");
      return new NewSkullHandler();
    } catch (ClassNotFoundException e) {
      return new OldSkullHandler();
    }
  }

  private static void setSkull(SkullMeta meta, String skull) {
    Optional<URL> url = Validate.getURL(skull);
    if (url.isPresent()) {
      skullHandler.setSkullByURL(meta, url.get());
      return;
    }

    if (MOJANG_SHA256_APPROX.matcher(skull).matches()) {
      skullHandler.setSkullByURL(meta, "https://textures.minecraft.net/texture/" + skull);
      return;
    }

    Optional<UUID> uuid = Validate.getUUID(skull);
    if (uuid.isPresent()) {
      skullHandler.setSkullByUUID(meta, uuid.get());
      return;
    }

    skullHandler.setSkullByName(meta, skull);
  }

  private static SkullMeta getSkullMeta(String skull) {
    SkullMeta meta = delegateSkullMeta.clone();
    setSkull(meta, skull);
    return meta;
  }

  @Override
  public @NotNull ItemMeta modifyMeta(@NotNull ItemMeta meta, @Nullable UUID uuid, @NotNull StringReplacer stringReplacer) {
    if (!(meta instanceof SkullMeta)) {
      return meta;
    }
    setSkull((SkullMeta) meta, stringReplacer.replaceOrOriginal(skullString, uuid));
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
  public Object toObject() {
    return skullString;
  }

  @Override
  public void loadFromObject(Object object) {
    this.skullString = String.valueOf(object);
  }

  private interface SkullHandler {
    @SuppressWarnings("deprecation")
    default void setSkullByName(SkullMeta meta, String name) {
      setSkullByPlayer(meta, Bukkit.getOfflinePlayer(name));
    }

    default void setSkullByUUID(SkullMeta meta, UUID uuid) {
      setSkullByPlayer(meta, Bukkit.getOfflinePlayer(uuid));
    }

    void setSkullByPlayer(SkullMeta meta, OfflinePlayer player);

    void setSkullByURL(SkullMeta meta, URL url);

    default void setSkullByURL(SkullMeta meta, String url) {
      try {
        setSkullByURL(meta, new URL(url));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    String getSkullValue(SkullMeta meta);
  }

  private static class OldSkullHandler implements SkullHandler {
    private final Method getProfileMethod;

    private OldSkullHandler() {
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

    @SuppressWarnings("deprecation")
    @Override
    public void setSkullByPlayer(SkullMeta meta, OfflinePlayer player) {
      if (VersionUtils.isAtLeast(12)) {
        meta.setOwningPlayer(player);
      } else {
        meta.setOwner(player.getName());
      }
    }

    @Override
    public void setSkullByURL(SkullMeta meta, URL url) {
      GameProfile profile = cache.computeIfAbsent(url.toString(), s -> {
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
        gameProfile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes())));
        return gameProfile;
      });

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

    @Override
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
  }

  private static class NewSkullHandler implements SkullHandler {
    private final Map<URL, PlayerProfile> profileMap = new HashMap<>();

    @Override
    public void setSkullByPlayer(SkullMeta meta, OfflinePlayer player) {
      meta.setOwningPlayer(player);
    }

    @Override
    public void setSkullByURL(SkullMeta meta, URL url) {
      PlayerProfile profile = profileMap.computeIfAbsent(url, url1 -> {
        PlayerProfile newProfile = Bukkit.createPlayerProfile(UUID.randomUUID(), "");
        PlayerTextures textures = newProfile.getTextures();
        textures.setSkin(url1);
        return newProfile;
      });
      meta.setOwnerProfile(profile);
    }

    @Override
    public String getSkullValue(SkullMeta meta) {
      PlayerProfile profile = meta.getOwnerProfile();
      if (profile == null) {
        return "";
      }

      PlayerTextures textures = profile.getTextures();
      URL url = textures.getSkin();
      if (url == null) {
        return "";
      }

      return url.toString();
    }
  }
}
