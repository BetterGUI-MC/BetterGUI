package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.task.element.TaskProcess;

import java.util.UUID;

public class TellAction extends BaseAction {
  public TellAction(ActionBuilder.Input input) {
    super(input);
  }

  @Override
  public void accept(UUID uuid, TaskProcess process) {
    MessageUtils.sendMessage(uuid, getReplacedString(uuid), "");
    process.next();
  }
}
