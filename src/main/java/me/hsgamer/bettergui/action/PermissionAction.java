package me.hsgamer.bettergui.action;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.CommandAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Optional;
import java.util.UUID;

public class PermissionAction extends CommandAction {
  /**
   * Create a new action
   *
   * @param string the action string
   */
  public PermissionAction(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(UUID uuid, TaskChain<?> taskChain) {
    Optional<Player> optional = Optional.ofNullable(Bukkit.getPlayer(uuid));
    if (!optional.isPresent()) {
      return;
    }

    String replacedString = getReplacedString(uuid);
    int spaceIndex = replacedString.indexOf(' ');
    Player player = optional.get();

    // Simply a Player Command if there is no space
    if (spaceIndex < 0) {
      taskChain.sync(() -> player.chat(getFinalCommand(replacedString)));
      return;
    }

    String permission = replacedString.substring(0, spaceIndex);
    String command = replacedString.substring(spaceIndex + 1).trim();

    if (player.hasPermission(permission)) {
      taskChain.sync(() -> player.chat(getFinalCommand(command)));
      return;
    }

    taskChain.sync(() -> {
      PermissionAttachment attachment = player.addAttachment(BetterGUI.getInstance(), permission, true);
      try {
        player.chat(getFinalCommand(command));
      } finally {
        player.removeAttachment(attachment);
      }
    });
  }
}
