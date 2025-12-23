package me.hsgamer.bettergui.modifier;

import io.github.projectunified.craftitem.spigot.skull.handler.SkullHandler;
import me.hsgamer.hscore.bukkit.item.modifier.ItemMetaModifier;
import me.hsgamer.hscore.common.StringReplacer;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SkullModifier implements ItemMetaModifier {
  private static final SkullHandler skullHandler = SkullHandler.getInstance();

  private String skullString = "";

  @Override
  public @NotNull ItemMeta modifyMeta(@NotNull ItemMeta meta, @Nullable UUID uuid, @NotNull StringReplacer stringReplacer) {
    if (meta instanceof SkullMeta && !skullString.isEmpty()) {
      skullHandler.setSkull((SkullMeta) meta, stringReplacer.replaceOrOriginal(skullString, uuid));
    }
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
}
