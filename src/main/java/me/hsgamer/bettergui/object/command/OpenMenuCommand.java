package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import java.util.Arrays;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.util.CommonUtils;
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
    if (BetterGUI.getInstance().getMenuManager().contains(menu)) {
      String[] finalArgs = args;
      Menu<?> parentMenu = null;

      Object object = getVariableManager().getParent();
      if (object instanceof Icon) {
        parentMenu = ((Icon) object).getMenu();
      } else if (object instanceof Menu) {
        parentMenu = (Menu<?>) object;
      }

      Menu<?> finalParentMenu = parentMenu;
      if (parentMenu != null) {
        taskChain.sync(() -> BetterGUI.getInstance().getMenuManager()
            .openMenu(menu, player, finalArgs, finalParentMenu, false));
      } else {
        taskChain.sync(() -> BetterGUI.getInstance().getMenuManager()
            .openMenu(menu, player, finalArgs, false));
      }
    } else {
      CommonUtils.sendMessage(player,
          BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.MENU_NOT_FOUND));
    }
  }
}
