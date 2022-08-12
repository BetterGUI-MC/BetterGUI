package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.util.CommandUtil;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PermissionAction extends BaseAction {
  public PermissionAction(ActionBuilder.Input input) {
    super(input);
  }

  @Override
  public void accept(UUID uuid, BatchRunnable.Process process) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      process.next();
      return;
    }

    String replacedString = getReplacedString(uuid);
    int spaceIndex = replacedString.indexOf(' ');

    // Simply a Player Command if there is no space
    if (spaceIndex < 0) {
      Bukkit.getScheduler().runTask(BetterGUI.getInstance(), () -> {
        player.chat(CommandUtil.normalizeCommand(replacedString));
        process.next();
      });
      return;
    }

    List<String> permissions = Arrays.asList(replacedString.substring(0, spaceIndex).split(";"));
    permissions.removeIf(player::hasPermission);
    String command = replacedString.substring(spaceIndex + 1).trim();

    Bukkit.getScheduler().runTask(BetterGUI.getInstance(), () -> {
      List<PermissionAttachment> attachments = permissions.stream()
        .map(s -> player.addAttachment(BetterGUI.getInstance(), s, true))
        .collect(Collectors.toList());
      try {
        player.chat(CommandUtil.normalizeCommand(command));
      } finally {
        attachments.forEach(player::removeAttachment);
      }
      process.next();
    });
  }

  @Override
  protected boolean shouldBeTrimmed() {
    return true;
  }
}
