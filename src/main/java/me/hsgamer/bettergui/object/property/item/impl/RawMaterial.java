package me.hsgamer.bettergui.object.property.item.impl;

import java.util.Optional;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RawMaterial extends ItemProperty<String, Optional<Material>> {

  public RawMaterial(Icon icon) {
    super(icon);
  }

  @Override
  public Optional<Material> getParsed(Player player) {
    try {
      return Optional.of(Material.valueOf(parseFromString(getValue(), player)));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public ItemStack parse(Player player, ItemStack parent) {
    getParsed(player).ifPresent(parent::setType);
    return parent;
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack item) {
    Optional<Material> parsed = getParsed(player);
    Material compare = item.getType();
    return parsed.isPresent() && compare.equals(parsed.get());
  }
}
