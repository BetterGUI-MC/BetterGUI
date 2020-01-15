package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import org.bukkit.entity.Player;

public class OpCommand extends Command {

  public OpCommand(Icon icon, String command) {
    super(icon, command);
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
