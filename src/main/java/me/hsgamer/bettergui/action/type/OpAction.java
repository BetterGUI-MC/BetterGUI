package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.CommandAction;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OpAction extends CommandAction {
  public OpAction(ActionBuilder.Input input) {
    super(input);
  }

  @Override
  public void accept(UUID uuid, TaskProcess process) {
    String replacedString = getFinalCommand(uuid);
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      process.next();
      return;
    }
    Bukkit.getScheduler().runTask(BetterGUI.getInstance(), () -> {
      if (player.isOp()) {
        player.chat(replacedString);
      } else {
        try {
          player.setOp(true);
          player.chat(replacedString);
        } finally {
          player.setOp(false);
        }
      }
      process.next();
    });
  }
}
