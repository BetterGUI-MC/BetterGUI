package me.hsgamer.bettergui.object.property.item.impl;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.bettergui.util.ExpressionUtils;
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Amount extends ItemProperty<Object, Integer> {

  public Amount(Icon icon) {
    super(icon);
  }

  @Override
  public Integer getParsed(Player player) {
    if (getValue() instanceof Integer) {
      return (Integer) getValue();
    } else {
      String value = (String) getValue();
      value = getIcon().hasVariables(value) ? getIcon().setVariables(value, player) : value;
      if (ExpressionUtils.isValidExpression(value)) {
        return ExpressionUtils.getResult(value).intValue();
      } else {
        if (Validate.isValidInteger(value)) {
          return Integer.parseInt(value);
        } else  {
          String error =
              ChatColor.RED + "Error parsing value!" + value + " is not a valid number";
          player.sendMessage(error);
          BetterGUI.getInstance().getLogger().warning(error);
          return 1;
        }
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
