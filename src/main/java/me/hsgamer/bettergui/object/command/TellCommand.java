package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.entity.Player;

public class TellCommand extends Command {

  public TellCommand(String string) {
    super(MessageUtils.colorize(string));
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    taskChain.sync(() -> player.sendMessage(getParsedCommand(player)));
  }
}
