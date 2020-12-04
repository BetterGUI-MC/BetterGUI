package me.hsgamer.bettergui.action;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.expression.ExpressionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Optional;
import java.util.UUID;

public class DelayAction extends BaseAction {
  /**
   * Create a new action
   *
   * @param string the action string
   */
  public DelayAction(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(UUID uuid, TaskChain<?> taskChain) {
    String value = getReplacedString(uuid);
    if (ExpressionUtils.isValidExpression(value)) {
      value = String.valueOf(ExpressionUtils.getResult(value).intValue());
    }
    String finalValue = value;
    if (!Validate.isValidPositiveNumber(finalValue)) {
      Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> player.sendMessage(ChatColor.RED + "Invalid delay: " + finalValue));
      return;
    }

    taskChain.delay(Integer.parseInt(finalValue));
  }
}
