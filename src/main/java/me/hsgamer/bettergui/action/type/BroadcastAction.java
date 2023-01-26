package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;

import java.util.UUID;

public class BroadcastAction extends BaseAction {
  public BroadcastAction(ActionBuilder.Input input) {
    super(input);
  }

  @Override
  public void accept(UUID uuid, TaskProcess process) {
    Bukkit.broadcastMessage(getReplacedString(uuid));
    process.next();
  }
}
