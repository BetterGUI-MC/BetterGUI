package me.hsgamer.bettergui.object.property.item.impl;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.ExpressionUtils;
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Durability extends ItemProperty<Object, Short> {

  public Durability(Icon icon) {
    super(icon);
  }

  @Override
  public Short getParsed(Player player) {
    String value = String.valueOf(getValue()).trim();
    value = getIcon().hasVariables(value) ? getIcon().setVariables(value, player) : value;
    if (ExpressionUtils.isValidExpression(value)) {
      return ExpressionUtils.getResult(value).shortValue();
    } else {
      if (Validate.isValidShort(value)) {
        return Short.parseShort(value);
      } else {
        CommonUtils.sendMessage(player, BetterGUI.getInstance().getMessageConfig().get(
            DefaultMessage.INVALID_NUMBER).replace("{input}", value));
        return 1;
      }
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
