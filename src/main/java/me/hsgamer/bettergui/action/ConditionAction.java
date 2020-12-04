package me.hsgamer.bettergui.action;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.hscore.expression.ExpressionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Optional;
import java.util.UUID;

public class ConditionAction extends BaseAction {
  /**
   * Create a new action
   *
   * @param string the action string
   */
  public ConditionAction(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(UUID uuid, TaskChain<?> taskChain) {
    String replacedString = getReplacedString(uuid);
    if (!ExpressionUtils.isBoolean(replacedString)) {
      Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> player.sendMessage(ChatColor.RED + "Invalid condition! Please inform the staff"));
      return;
    }

    if (ExpressionUtils.getResult(replacedString).intValue() != 1) {
      taskChain.sync(TaskChain::abort);
    }
  }
}
