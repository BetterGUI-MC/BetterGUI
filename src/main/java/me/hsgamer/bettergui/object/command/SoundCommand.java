package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import com.cryptomorin.xseries.XSound;
import java.util.Objects;
import me.hsgamer.bettergui.object.Command;
import org.bukkit.entity.Player;

public class SoundCommand extends Command {

  public SoundCommand(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    String parsed = getParsedCommand(player);
    taskChain.syncFuture(() -> Objects.requireNonNull(XSound.play(player, parsed))
        .thenApply(vo -> "sound complete"));
  }
}
