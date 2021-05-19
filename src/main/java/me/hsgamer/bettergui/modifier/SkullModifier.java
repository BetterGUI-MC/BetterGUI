package me.hsgamer.bettergui.modifier;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.hscore.bukkit.item.ItemMetaModifier;
import me.hsgamer.hscore.bukkit.utils.BukkitUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SkullModifier extends ItemMetaModifier {
  private static final boolean IS_PAPER;

  static {
    IS_PAPER = Validate.isClassLoaded("com.destroystokyo.paper.PaperConfig");
  }

  private String skullString = "";

  @Override
  public String getName() {
    return "skull";
  }

  @Override
  public ItemMeta modifyMeta(ItemMeta meta, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    if (meta instanceof SkullMeta) {
      String value = StringReplacer.replace(skullString, uuid, stringReplacerMap.values());
      if (Boolean.TRUE.equals(MainConfig.LOAD_PLAYER_SKULL_ASYNC.getValue()) && BukkitUtils.isUsername(value)) {
        CompletableFuture<OfflinePlayer> completableFuture = BukkitUtils.getOfflinePlayerAsync(value);
        if (completableFuture.isDone()) {
          OfflinePlayer offlinePlayer = completableFuture.join();
          if (IS_PAPER && XMaterial.getVersion() >= 12 && offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();
            if (player.getPlayerProfile().hasTextures()) {
              ((SkullMeta) meta).setPlayerProfile(player.getPlayerProfile());
            }
          } else {
            return SkullUtils.applySkin(meta, offlinePlayer);
          }
        }
      } else {
        return SkullUtils.applySkin(meta, value);
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
