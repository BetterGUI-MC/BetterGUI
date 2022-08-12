package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.Bukkit;

import java.util.UUID;

public class BroadcastAction extends BaseAction {
  public BroadcastAction(ActionBuilder.Input input) {
    super(input);
  }

  @Override
  public void accept(UUID uuid, BatchRunnable.Process process) {
    Bukkit.broadcastMessage(StringReplacerApplier.COLORIZE.replace(getReplacedString(uuid)));
    process.next();
  }
}
