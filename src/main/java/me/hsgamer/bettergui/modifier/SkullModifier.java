package me.hsgamer.bettergui.modifier;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemMetaModifier;
import me.hsgamer.hscore.bukkit.utils.BukkitUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SkullModifier extends ItemMetaModifier {
  private static boolean playerProfileSupport;
  private static Method getProfileMethod = null;
  private static Method setProfileMethod = null;
  private static Method isCompleteMethod = null;
  static {
    loadProfileClass();
  }

  private static void loadProfileClass() {
    boolean paperProfileSupport = Validate.isClassLoaded("com.destroystokyo.paper.profile.PlayerProfile");
    boolean spigotProfileSupport = Validate.isClassLoaded("org.bukkit.profile.PlayerProfile");
    playerProfileSupport = paperProfileSupport || spigotProfileSupport;

    if (playerProfileSupport) {
      try {
        getProfileMethod = Player.class.getDeclaredMethod("getPlayerProfile");
      } catch (NoSuchMethodException ignored) {
        playerProfileSupport = false;
        return;
      }

      Class<?> playerProfileClass;
      if (spigotProfileSupport) {
        try {
          playerProfileClass = Class.forName("org.bukkit.profile.PlayerProfile");
          setProfileMethod = SkullMeta.class.getDeclaredMethod("setOwnerProfile", playerProfileClass);
          isCompleteMethod = playerProfileClass.getDeclaredMethod("isComplete");
        } catch (Exception ignored) {
          playerProfileSupport = false;
          return;
        }
      }

      if (paperProfileSupport) {
        try {
          playerProfileClass = Class.forName("com.destroystokyo.paper.profile.PlayerProfile");
          setProfileMethod = SkullMeta.class.getDeclaredMethod("setPlayerProfile", playerProfileClass);
          isCompleteMethod = playerProfileClass.getDeclaredMethod("hasTextures");
        } catch (Exception ignored) {
          playerProfileSupport = false;
        }
      }
    }
  }

  private String skullString = "";

  @Override
  public String getName() {
    return "skull";
  }

  @Override
  public ItemMeta modifyMeta(ItemMeta meta, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    if (!(meta instanceof SkullMeta)) {
      return meta;
    }
    String value = StringReplacer.replace(skullString, uuid, stringReplacerMap.values());
    if (!BukkitUtils.isUsername(value)) {
      return SkullUtils.applySkin(meta, value);
    }
    Player player = Bukkit.getPlayer(value);
    if (player != null) {
      if (!playerProfileSupport || XMaterial.getVersion() < 12) {
        return SkullUtils.applySkin(meta, player);
      }
      try {
        Object profile = getProfileMethod.invoke(player);
        if ((boolean) isCompleteMethod.invoke(profile)) {
          SkullMeta skullMeta = (SkullMeta) meta;
          setProfileMethod.invoke(skullMeta, profile);
        }
      } catch (Exception e) {
        return SkullUtils.applySkin(meta, player);
      }
    } else {
      CompletableFuture<OfflinePlayer> completableFuture = BukkitUtils.getOfflinePlayerAsync(value);
      if (completableFuture.isDone()) {
        return SkullUtils.applySkin(meta, completableFuture.join());
      }
    }
    return meta;
  }

  @Override
  public void loadFromItemMeta(ItemMeta meta) {
    this.skullString = Optional.ofNullable(SkullUtils.getSkinValue(meta)).orElse("");
  }

  @Override
  public boolean canLoadFromItemMeta(ItemMeta meta) {
    return meta instanceof SkullMeta;
  }

  @Override
  public boolean compareWithItemMeta(ItemMeta meta, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
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
}
