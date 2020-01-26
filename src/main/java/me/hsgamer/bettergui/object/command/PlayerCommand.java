package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import org.bukkit.entity.Player;

public class PlayerCommand extends Command {

  public PlayerCommand(String command) {
    super(command);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    taskChain.sync(() -> player.chat('/' + getParsedCommand(player)));
  }
}
