package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.util.ExpressionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ConditionCommand extends Command {

  public ConditionCommand(String command) {
    super(command);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    String parsed = getParsedCommand(player);
    if (!ExpressionUtils.isBoolean(parsed)) {
      player.sendMessage(ChatColor.RED + "Invalid condition! Please inform the staff");
      return;
    }

    if (ExpressionUtils.getResult(parsed).intValue() != 1) {
      taskChain.sync(TaskChain::abort);
    }
  }

}
