package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.entity.Player;

public class OpenMenuCommand extends Command {

  public OpenMenuCommand(String command) {
    super(command);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    String parsed = getParsedCommand(player);
    if (BetterGUI.getInstance().getMenuManager().contains(parsed)) {
      Object icon = getVariableManager().getParent();
      if (icon instanceof Icon) {
        taskChain.sync(() -> BetterGUI.getInstance().getMenuManager()
            .openMenu(parsed, player, ((Icon) icon).getMenu(), false));
      } else {
        taskChain
            .sync(() -> BetterGUI.getInstance().getMenuManager().openMenu(parsed, player, false));
      }
    } else {
      CommonUtils.sendMessage(player,
          BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.MENU_NOT_FOUND));
    }
  }
}
