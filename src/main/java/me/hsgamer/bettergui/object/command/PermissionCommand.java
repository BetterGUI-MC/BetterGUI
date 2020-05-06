package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.object.Command;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class PermissionCommand extends Command {

  public PermissionCommand(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    String parsed = getParsedCommand(player);
    int spaceIndex = parsed.indexOf(' ');

    // Simply a Player Command if there is no space
    if (spaceIndex == -1) {
      taskChain.sync(() -> player.chat("/" + parsed));
      return;
    }

    String permission = parsed.substring(0, spaceIndex);
    String command = parsed.substring(spaceIndex + 1).trim();

    if (player.hasPermission(permission)) {
      taskChain.sync(() -> player.chat("/" + command));
      return;
    }

    taskChain.sync(() -> {
      PermissionAttachment attachment = player
          .addAttachment(BetterGUI.getInstance(), permission, true);
      try {
        player.chat("/" + command);
      } finally {
        player.removeAttachment(attachment);
      }
    });
  }
}
