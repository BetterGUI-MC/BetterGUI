package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Optional;
import java.util.UUID;

public class DelayAction extends BaseAction {
  public DelayAction(ActionBuilder.Input input) {
    super(input);
  }

  @Override
  public void accept(UUID uuid, BatchRunnable.Process process) {
    String value = getReplacedString(uuid);
    if (Validate.isValidPositiveNumber(value)) {
      Bukkit.getScheduler().runTaskLater(BetterGUI.getInstance(), process::next, Long.parseLong(value));
    } else {
      Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> player.sendMessage(ChatColor.RED + "Invalid delay: " + value));
      process.next();
    }
  }

  @Override
  protected boolean shouldBeTrimmed() {
    return true;
  }
}
