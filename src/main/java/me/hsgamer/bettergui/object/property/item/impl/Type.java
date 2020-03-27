package me.hsgamer.bettergui.object.property.item.impl;

import com.cryptomorin.xseries.XMaterial;
import java.util.Optional;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Type extends ItemProperty<String, Optional<XMaterial>> {

  public Type(Icon icon) {
    super(icon);
  }

  @Override
  public Optional<XMaterial> getParsed(Player player) {
    String value = getValue();
    value = getIcon().hasVariables(value) ? getIcon().setVariables(value, player) : value;
    return XMaterial.matchXMaterial(value);
  }

  @Override
  public ItemStack parse(Player player, ItemStack parent) {
    Optional<XMaterial> parsed = getParsed(player);
    parsed.ifPresent(xMaterial -> xMaterial.setType(parent));
    return parent;
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack item) {
    Optional<XMaterial> material = getParsed(player);
    return material.isPresent() && material.get().isSimilar(item);
  }
}
