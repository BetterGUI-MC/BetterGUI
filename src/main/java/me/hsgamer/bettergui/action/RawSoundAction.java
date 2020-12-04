package me.hsgamer.bettergui.action;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.api.action.BaseAction;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.UUID;

public class RawSoundAction extends BaseAction {
  /**
   * Create a new action
   *
   * @param string the action string
   */
  public RawSoundAction(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(UUID uuid, TaskChain<?> taskChain) {
    String sound;
    float volume = 1f;
    float pitch = 1f;
    String[] split = getReplacedString(uuid).split(",", 3);

    sound = split[0].trim();
    if (split.length > 1) {
      try {
        volume = Float.parseFloat(split[1].trim());
      } catch (NumberFormatException ignored) {
        // IGNORED
      }
    }
    if (split.length > 2) {
      try {
        pitch = Float.parseFloat(split[2].trim());
      } catch (NumberFormatException ignored) {
        // IGNORED
      }
    }

    float finalVolume = volume;
    float finalPitch = pitch;
    Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> taskChain.sync(() -> player.playSound(player.getLocation(), sound, finalVolume, finalPitch)));
  }
}
