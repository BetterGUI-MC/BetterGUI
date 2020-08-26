package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BroadcastCommand extends Command {

  public BroadcastCommand(String string) {
    super(MessageUtils.colorize(string));
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    taskChain.sync(() -> Bukkit.broadcastMessage(getParsedCommand(player)));
  }
}
