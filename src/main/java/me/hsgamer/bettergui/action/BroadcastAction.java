package me.hsgamer.bettergui.action;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.utils.CommonStringReplacers;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.Bukkit;

import java.util.UUID;

public class BroadcastAction extends BaseAction {
  /**
   * Create a new action
   *
   * @param string the action string
   */
  public BroadcastAction(String string) {
    super(MessageUtils.colorize(string));
  }

  @Override
  public void addToTaskChain(UUID uuid, TaskChain<?> taskChain) {
    taskChain.sync(() -> Bukkit.broadcastMessage(CommonStringReplacers.COLORIZE.replace(getReplacedString(uuid))));
  }
}
