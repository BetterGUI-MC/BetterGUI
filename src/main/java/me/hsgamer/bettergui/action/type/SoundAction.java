package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SoundAction extends BaseAction {
  public SoundAction(ActionBuilder.Input input) {
    super(input);
  }

  @Override
  public void accept(UUID uuid, BatchRunnable.Process process) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      process.next();
      return;
    }

    String sound;
    float volume = 1f;
    float pitch = 1f;
    String replaced = getReplacedString(uuid);
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
    Bukkit.getScheduler().runTask(BetterGUI.getInstance(), () -> {
      try {
        Sound soundEnum = Sound.valueOf(sound.replace(" ", "_").toUpperCase());
        player.playSound(player.getLocation(), soundEnum, finalVolume, finalPitch);
      } catch (Exception exception) {
        player.playSound(player.getLocation(), sound, finalVolume, finalPitch);
      }
      process.next();
    });
  }

  @Override
  protected boolean shouldBeTrimmed() {
    return true;
  }
}
