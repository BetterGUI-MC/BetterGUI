package me.hsgamer.bettergui.object.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import co.aikar.taskchain.TaskChain;
import java.util.Arrays;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.entity.Player;

public class OpenMenuCommand extends Command {

  public OpenMenuCommand(String command) {
    super(command);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    // Get Menu and Arguments
    String[] split = getParsedCommand(player).split(" ");
    String menu = split[0];
    String[] args = new String[0];
    if (split.length > 1) {
      args = Arrays.copyOfRange(split, 1, split.length);
    }

    // Open menu
    if (getInstance().getMenuManager().contains(menu)) {
      String[] finalArgs = args;
      Menu<?> parentMenu = null;

      Object object = getVariableManager().getParent();
      if (object instanceof Icon) {
        parentMenu = ((Icon) object).getMenu();
      } else if (object instanceof Menu) {
        parentMenu = (Menu<?>) object;
      }

      Menu<?> finalParentMenu = parentMenu;
      Runnable runnable;
      if (parentMenu != null) {
        runnable = () -> getInstance().getMenuManager()
            .openMenu(menu, player, finalArgs, finalParentMenu, false);
      } else {
        runnable = () -> getInstance().getMenuManager()
            .openMenu(menu, player, finalArgs, false);
      }
      taskChain.sync(
          () -> getInstance().getServer().getScheduler()
              .scheduleSyncDelayedTask(getInstance(), runnable));
    } else {
      MessageUtils.sendMessage(player, MessageConfig.MENU_NOT_FOUND.getValue());
    }
  }
}
