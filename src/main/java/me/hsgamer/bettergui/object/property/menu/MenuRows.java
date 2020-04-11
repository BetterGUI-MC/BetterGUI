package me.hsgamer.bettergui.object.property.menu;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.property.MenuProperty;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.ExpressionUtils;
import org.bukkit.entity.Player;

public class MenuRows extends MenuProperty<Object, Integer> {

  public MenuRows(Menu<?> menu) {
    super(menu);
  }

  @Override
  public Integer getParsed(Player player) {
    String parsed = parseFromString(String.valueOf(getValue()), player);
    if (ExpressionUtils.isValidExpression(parsed)) {
      return ExpressionUtils.getResult(parsed).intValue() * 9;
    } else {
      CommonUtils.sendMessage(player,
          BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.INVALID_NUMBER)
              .replace("{input}", parsed));
      return 27;
    }
  }
}
