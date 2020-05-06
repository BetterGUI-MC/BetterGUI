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
    String parsed = getParsedCommand(player);
    if (player.isOp()) {
      taskChain.sync(() -> player.chat("/" + parsed));
    } else {
      taskChain.sync(() -> {
        try {
          player.setOp(true);
          player.chat("/" + parsed);
        } finally {
          player.setOp(false);
        }
      });
    }
  }
}
