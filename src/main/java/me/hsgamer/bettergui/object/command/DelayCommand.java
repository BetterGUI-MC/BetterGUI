package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.ExpressionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DelayCommand extends Command {

  public DelayCommand(String command) {
    super(command);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    String value = getParsedCommand(player);
    if (ExpressionUtils.isValidExpression(value)) {
      value = String.valueOf(ExpressionUtils.getResult(value).intValue());
    }
    if (!CommonUtils.isValidPositiveInteger(value)) {
      player.sendMessage(ChatColor.RED + "Invalid delay: " + value);
      return;
    }

    taskChain.delay(Integer.parseInt(value));
  }
}
