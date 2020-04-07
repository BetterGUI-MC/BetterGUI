package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.object.Command;
import org.bukkit.entity.Player;

public class UpdateMenuCommand extends Command {

  public UpdateMenuCommand(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    getIcon().ifPresent(icon -> taskChain.sync(() -> icon.getMenu().updateInventory(player)));
  }
}
