package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.CommandAction;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerAction extends CommandAction {
  public PlayerAction(ActionBuilder.Input input) {
    super(input);
  }

  @Override
  public void accept(UUID uuid, BatchRunnable.Process process) {
    Bukkit.getScheduler().runTask(BetterGUI.getInstance(), () -> {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        player.chat(getFinalCommand(uuid));
      }
      process.next();
    });
  }
}
