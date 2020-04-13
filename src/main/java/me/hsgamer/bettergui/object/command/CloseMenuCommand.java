package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import org.bukkit.entity.Player;

public class CloseMenuCommand extends Command {

  public CloseMenuCommand(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    Object object = getVariableManager().getParent();
    if (object instanceof Icon) {
      taskChain.sync(() -> ((Icon) object).getMenu().closeInventory(player));
    } else if (object instanceof Menu) {
      taskChain.sync(() -> ((Menu<?>) object).closeInventory(player));
    }
  }
}
