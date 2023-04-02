package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;

import java.util.UUID;

public class ConsoleAction extends BaseAction {
  public ConsoleAction(ActionBuilder.Input input) {
    super(input);
  }

  @Override
  public void accept(UUID uuid, TaskProcess process) {
    Scheduler.CURRENT.runTask(BetterGUI.getInstance(), () -> {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getReplacedString(uuid));
      process.next();
    }, false);
  }
}
