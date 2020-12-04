package me.hsgamer.bettergui.action;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.api.action.CommandAction;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.UUID;

public class OpAction extends CommandAction {
  /**
   * Create a new action
   *
   * @param string the action string
   */
  public OpAction(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(UUID uuid, TaskChain<?> taskChain) {
    String replacedString = getFinalCommand(getReplacedString(uuid));
    Optional.ofNullable(Bukkit.getPlayer(uuid))
      .ifPresent(player -> {
        if (player.isOp()) {
          taskChain.sync(() -> player.chat(replacedString));
        } else {
          taskChain.sync(() -> {
            try {
              player.setOp(true);
              player.chat(replacedString);
            } finally {
              player.setOp(false);
            }
          });
        }
      });
  }
}
