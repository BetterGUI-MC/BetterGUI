package me.hsgamer.bettergui.object.property.item.impl;

import me.hsgamer.bettergui.config.impl.MessageConfig;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.hscore.expression.ExpressionUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Amount extends ItemProperty<Object, Integer> {

  public Amount(Icon icon) {
    super(icon);
  }

  @Override
  public Integer getParsed(Player player) {
    String value = parseFromString(String.valueOf(getValue()).trim(), player);
    if (ExpressionUtils.isValidExpression(value)) {
      return ExpressionUtils.getResult(value).intValue();
    } else {
      CommonUtils
          .sendMessage(player, MessageConfig.INVALID_NUMBER.getValue().replace("{input}", value));
      return 1;
    }
  }

  @Override
  public ItemStack parse(Player player, ItemStack parent) {
    parent.setAmount(getParsed(player));
    return parent;
  }

  @Override
  public boolean compareWithItemStack(Player player, ItemStack item) {
    return item.getAmount() >= getParsed(player);
  }
}
