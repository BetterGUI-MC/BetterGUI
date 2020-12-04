package me.hsgamer.bettergui.action;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.api.action.BaseAction;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.UUID;

public class TellAction extends BaseAction {
  /**
   * Create a new action
   *
   * @param string the action string
   */
  public TellAction(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(UUID uuid, TaskChain<?> taskChain) {
    String replacedString = getReplacedString(uuid);
    Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> taskChain.sync(() -> player.sendMessage(replacedString)));
  }
}
