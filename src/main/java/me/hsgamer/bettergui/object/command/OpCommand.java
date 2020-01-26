package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.object.Command;
import org.bukkit.entity.Player;

public class OpCommand extends Command {

  public OpCommand(String command) {
    super(command);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    taskChain.sync(() -> {
      if (player.isOp()) {
        player.chat("/" + getParsedCommand(player));

      } else {
        try {
          player.setOp(true);
          player.chat("/" + getParsedCommand(player));
        } finally {
          player.setOp(false);
        }
      }
    });
  }
}
