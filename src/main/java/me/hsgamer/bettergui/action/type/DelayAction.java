package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DelayAction extends BaseAction {
  public DelayAction(ActionBuilder.Input input) {
    super(input);
  }

  @Override
  public void accept(UUID uuid, TaskProcess process) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      process.next();
      return;
    }

    String value = getReplacedString(uuid);
    if (!Validate.isValidPositiveNumber(value)) {
      player.sendMessage(ChatColor.RED + "Invalid delay: " + value);
      process.next();
    }

    Scheduler.CURRENT.runEntityTaskLater(BetterGUI.getInstance(), player, process::next, process::next, Long.parseLong(value), true);
  }
}
