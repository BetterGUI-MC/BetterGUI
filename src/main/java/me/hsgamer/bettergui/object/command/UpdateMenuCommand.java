package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import org.bukkit.entity.Player;

public class UpdateMenuCommand extends Command {

  public UpdateMenuCommand(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    Object object = getVariableManager().getParent();
    if (object instanceof Icon) {
      taskChain.sync(() -> ((Icon) object).getMenu().updateInventory(player));
    }
  }
}
