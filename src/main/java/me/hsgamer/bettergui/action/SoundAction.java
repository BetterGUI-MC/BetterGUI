package me.hsgamer.bettergui.action;

import co.aikar.taskchain.TaskChain;
import com.cryptomorin.xseries.XSound;
import me.hsgamer.bettergui.api.action.BaseAction;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SoundAction extends BaseAction {
  /**
   * Create a new action
   *
   * @param string the action string
   */
  public SoundAction(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(UUID uuid, TaskChain<?> taskChain) {
    String replacedString = getReplacedString(uuid);
    Optional.ofNullable(Bukkit.getPlayer(uuid))
      .ifPresent(player -> taskChain.syncFuture(() -> Objects.requireNonNull(XSound.play(player, replacedString)).thenApply(vo -> "sound complete")));
  }
}
