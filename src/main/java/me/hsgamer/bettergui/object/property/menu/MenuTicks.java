package me.hsgamer.bettergui.object.property.menu;

import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.property.MenuProperty;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.expression.ExpressionUtils;
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
      MessageUtils
          .sendMessage(player, MessageConfig.INVALID_NUMBER.getValue().replace("{input}", parsed));
      return 0L;
    }
  }
}
