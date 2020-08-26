package me.hsgamer.bettergui.object.property.item.impl;

import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.expression.ExpressionUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Durability extends ItemProperty<Object, Short> {

  public Durability(Icon icon) {
    super(icon);
  }

  @Override
  public Short getParsed(Player player) {
    String value = parseFromString(String.valueOf(getValue()).trim(), player);
    if (ExpressionUtils.isValidExpression(value)) {
      return ExpressionUtils.getResult(value).shortValue();
    } else {
      MessageUtils
          .sendMessage(player, MessageConfig.INVALID_NUMBER.getValue().replace("{input}", value));
      return 1;
    }
  }

  @Override
  public ItemStack parse(Player player, ItemStack parent) {
    parent.setDurability(getParsed(player));
    return parent;
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack item) {
    return item.getDurability() == getParsed(player);
  }
}
