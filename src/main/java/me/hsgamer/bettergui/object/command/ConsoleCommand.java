package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.object.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConsoleCommand extends Command {

  public ConsoleCommand(String command) {
    super(command);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    taskChain
        .sync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getParsedCommand(player)));
  }
}
