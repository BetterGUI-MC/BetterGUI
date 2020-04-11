package me.hsgamer.bettergui.object.property.menu;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.CommandBuilder;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.property.MenuProperty;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.entity.Player;

public class MenuCloseAction extends MenuProperty<Object, TaskChain<?>> {

  public MenuCloseAction(Menu<?> menu) {
    super(menu);
  }

  @Override
  public TaskChain<?> getParsed(Player player) {
    TaskChain<?> taskChain = BetterGUI.newChain();
    CommandBuilder.getCommands(null,
        CommonUtils.createStringListFromObject(getValue(), true))
        .forEach(command -> command.addToTaskChain(player, taskChain));
    return taskChain;
  }
}
