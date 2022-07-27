package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class OpenMenuAction extends BaseAction {
  public OpenMenuAction(ActionBuilder.Input input) {
    super(input);
  }

  @Override
  public void accept(UUID uuid, BatchRunnable.Process process) {
    // Get Menu and Arguments
    String[] split = getReplacedString(uuid).split(" ");
    String menu = split[0];
    String[] args = new String[0];
    if (split.length > 1) {
      args = Arrays.copyOfRange(split, 1, split.length);
    }

    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      process.next();
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
      getInstance().getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), () -> {
        runnable.run();
        process.next();
      });
    } else {
      MessageUtils.sendMessage(player, getInstance().getMessageConfig().menuNotFound);
      process.next();
    }
  }
}
