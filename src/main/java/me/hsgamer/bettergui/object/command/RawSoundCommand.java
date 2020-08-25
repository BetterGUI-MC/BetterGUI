package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.object.Command;
import org.bukkit.entity.Player;

public class RawSoundCommand extends Command {

  public RawSoundCommand(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    String sound;
    float volume = 1f;
    float pitch = 1f;
    String[] split = getParsedCommand(player).split(",");

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
    taskChain.sync(() -> player.playSound(player.getLocation(), sound, finalVolume, finalPitch));
  }
}
