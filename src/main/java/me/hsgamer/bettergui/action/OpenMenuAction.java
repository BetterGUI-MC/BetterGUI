package me.hsgamer.bettergui.action;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class OpenMenuAction extends BaseAction {
  /**
   * Create a new action
   *
   * @param string the action string
   */
  public OpenMenuAction(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(UUID uuid, TaskChain<?> taskChain) {
    // Get Menu and Arguments
    String[] split = getReplacedString(uuid).split(" ");
    String menu = split[0];
    String[] args = new String[0];
    if (split.length > 1) {
      args = Arrays.copyOfRange(split, 1, split.length);
    }

    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      return;
    }

    // Open menu
    if (getInstance().getMenuManager().contains(menu)) {
      String[] finalArgs = args;
      Runnable runnable;
      Menu parentMenu = getMenu();
      if (parentMenu != null) {
        runnable = () -> getInstance().getMenuManager().openMenu(menu, player, finalArgs, parentMenu, false);
      } else {
        runnable = () -> getInstance().getMenuManager().openMenu(menu, player, finalArgs, false);
      }
      taskChain.sync(() -> getInstance().getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), runnable));
    } else {
      MessageUtils.sendMessage(player, MessageConfig.MENU_NOT_FOUND.getValue());
    }
  }
}
