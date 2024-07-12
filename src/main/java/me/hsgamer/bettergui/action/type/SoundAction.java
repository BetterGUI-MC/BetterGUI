package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.action.common.Action;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SoundAction implements Action {
  private final String value;

  public SoundAction(String value) {
    this.value = value;
  }

  @Override
  public void apply(UUID uuid, TaskProcess process, StringReplacer stringReplacer) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      process.next();
      return;
    }

    String sound;
    float volume = 1f;
    float pitch = 1f;
    String replaced = stringReplacer.replaceOrOriginal(value, uuid);
    String[] split;
    if (replaced.indexOf(',') != -1) {
      split = replaced.split(",");
    } else {
      split = replaced.split(" ");
    }

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
    SchedulerUtil.entity(player)
      .run(() -> {
        try {
          Sound soundEnum = Sound.valueOf(sound.replace(" ", "_").toUpperCase());
          player.playSound(player.getLocation(), soundEnum, finalVolume, finalPitch);
        } catch (Exception exception) {
          player.playSound(player.getLocation(), sound, finalVolume, finalPitch);
        } finally {
          process.next();
        }
      }, process::next);
  }
}
