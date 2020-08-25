package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import com.cryptomorin.xseries.NoteBlockMusic;
import me.hsgamer.bettergui.object.Command;
import org.bukkit.entity.Player;

public class MusicCommand extends Command {

  public MusicCommand(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    String parsed = getParsedCommand(player);
    taskChain.syncFuture(
        () -> NoteBlockMusic.playMusic(player, player.getLocation(), parsed)
            .thenApply(vo -> "music complete"));
  }
}
