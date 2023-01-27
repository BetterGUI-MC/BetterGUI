package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.CommandAction;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PermissionAction extends CommandAction {
  public PermissionAction(ActionBuilder.Input input) {
    super(input);
  }

  @Override
  public void accept(UUID uuid, TaskProcess process) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      process.next();
      return;
    }

    List<String> permissions = new ArrayList<>(input.getOptionAsList());
    permissions.removeIf(player::hasPermission);
    String replacedString = getFinalCommand(uuid);

    Bukkit.getScheduler().runTask(BetterGUI.getInstance(), () -> {
      List<PermissionAttachment> attachments = permissions.stream()
        .map(s -> player.addAttachment(BetterGUI.getInstance(), s, true))
        .collect(Collectors.toList());
      try {
        player.chat(replacedString);
      } finally {
        attachments.forEach(player::removeAttachment);
      }
      process.next();
    });
  }
}
