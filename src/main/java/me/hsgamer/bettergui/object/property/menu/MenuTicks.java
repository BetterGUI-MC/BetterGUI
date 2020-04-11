package me.hsgamer.bettergui.object.property.menu;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.property.MenuProperty;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.ExpressionUtils;
import org.bukkit.entity.Player;

public class MenuTicks extends MenuProperty<Object, Long> {

  public MenuTicks(Menu<?> menu) {
    super(menu);
  }

  @Override
  public Long getParsed(Player player) {
    String parsed = parseFromString(String.valueOf(getValue()), player);
    if (ExpressionUtils.isValidExpression(parsed)) {
      return ExpressionUtils.getResult(parsed).longValue();
    } else {
      CommonUtils.sendMessage(player,
          BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.INVALID_NUMBER)
              .replace("{input}", parsed));
      return 0L;
    }
  }
}
