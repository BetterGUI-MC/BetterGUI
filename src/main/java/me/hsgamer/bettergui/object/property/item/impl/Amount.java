package me.hsgamer.bettergui.object.property.item.impl;

import java.math.BigDecimal;
import java.util.Optional;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.ExpressionUtils;
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Amount extends ItemProperty<Object, Integer> {

  public Amount(Icon icon) {
    super(icon);
  }

  @Override
  public Integer getParsed(Player player) {
    String value = String.valueOf(getValue()).trim();
    value = getIcon().hasVariables(value) ? getIcon().setVariables(value, player) : value;
    if (ExpressionUtils.isValidExpression(value)) {
      return ExpressionUtils.getResult(value).intValue();
    } else {
      Optional<BigDecimal> number = Validate.getNumber(value);
      if (number.isPresent()) {
        return number.get().intValue();
      } else {
        CommonUtils.sendMessage(player, BetterGUI.getInstance().getMessageConfig().get(
            DefaultMessage.INVALID_NUMBER).replace("{input}", value));
        return 1;
      }
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
