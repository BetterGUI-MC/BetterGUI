package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.ActionBuilder;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.List;
import java.util.stream.Collectors;

public class PermissionAction extends CommandAction {
  public PermissionAction(ActionBuilder.Input input) {
    super(input);
  }

  @Override
  protected void accept(Player player, String command) {
    List<PermissionAttachment> attachments = input.getOptionAsList().stream()
      .filter(s -> !player.hasPermission(s))
      .map(s -> player.addAttachment(BetterGUI.getInstance(), s, true))
      .collect(Collectors.toList());
    try {
      player.chat(command);
    } finally {
      attachments.forEach(player::removeAttachment);
    }
  }
}
