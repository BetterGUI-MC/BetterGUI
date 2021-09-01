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

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SkullModifier extends ItemMetaModifier {
  private static final boolean PLAYER_PROFILE_SUPPORT;

  static {
    PLAYER_PROFILE_SUPPORT = Validate.isMethodLoaded(Player.class.getName(), "getPlayerProfile");
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
      if (!PLAYER_PROFILE_SUPPORT || XMaterial.getVersion() < 12) {
        return SkullUtils.applySkin(meta, player);
      }
      if (player.getPlayerProfile().hasTextures()) {
        ((SkullMeta) meta).setPlayerProfile(player.getPlayerProfile());
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
