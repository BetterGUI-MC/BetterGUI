package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.object.Command;
import org.bukkit.entity.Player;

public class OpenMenuCommand extends Command {

  public OpenMenuCommand(String command) {
    super(command);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    String parsed = getParsedCommand(player);
    if (BetterGUI.getInstance().getMenuManager().contains(parsed)) {
      taskChain.sync(() -> BetterGUI.getInstance().getMenuManager().openMenu(parsed, player));
    } else {
      // TODO: Config
    }
  }
}
