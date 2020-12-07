package me.hsgamer.bettergui.modifier;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemModifier;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class XMaterialModifier implements ItemModifier {
  private String materialString;

  @Override
  public String getName() {
    return "material";
  }

  @Override
  public ItemStack modify(ItemStack original, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    XMaterial.matchXMaterial(StringReplacer.replace(materialString, uuid, stringReplacerMap.values()))
      .ifPresent(xMaterial -> xMaterial.setType(original));
    return original;
  }

  @Override
  public Object toObject() {
    return materialString;
  }

  @Override
  public void loadFromObject(Object object) {
    this.materialString = String.valueOf(object);
  }

  @Override
  public void loadFromItemStack(ItemStack itemStack) {
    this.materialString = XMaterial.matchXMaterial(itemStack).name();
  }

  @Override
  public boolean compareWithItemStack(ItemStack itemStack, UUID uuid, Map<String, StringReplacer> stringReplacerMap) {
    return XMaterial
      .matchXMaterial(StringReplacer.replace(materialString, uuid, stringReplacerMap.values()))
      .map(xMaterial -> xMaterial.isSimilar(itemStack))
      .orElse(false);
  }
}
