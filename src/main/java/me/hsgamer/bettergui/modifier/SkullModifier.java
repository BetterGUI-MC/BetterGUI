package me.hsgamer.bettergui.modifier;

import com.cryptomorin.xseries.SkullUtils;
import me.hsgamer.hscore.bukkit.item.ItemModifier;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SkullModifier implements ItemModifier {
  private String skullString = "";

  @Override
  public String getName() {
    return "skull";
  }

  @Override
  public ItemStack modify(ItemStack original, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    ItemMeta itemMeta = original.getItemMeta();
    if (itemMeta instanceof SkullMeta) {
      original.setItemMeta(SkullUtils.applySkin(itemMeta, StringReplacer.replace(skullString, uuid, stringReplacerMap.values())));
    }
    return original;
  }

  @Override
  public Object toObject() {
    return skullString;
  }

  @Override
  public void loadFromObject(Object object) {
    this.skullString = String.valueOf(object);
  }

  @Override
  public boolean canLoadFromItemStack(ItemStack itemStack) {
    return itemStack.getItemMeta() instanceof SkullMeta;
  }

  @Override
  public void loadFromItemStack(ItemStack itemStack) {
    this.skullString = Optional.of(itemStack.getItemMeta()).map(SkullUtils::getSkinValue).orElse("");
  }

  @Override
  public boolean compareWithItemStack(ItemStack itemStack, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    return false;
  }
}
