package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class OpenMenuAction extends BaseAction {
  private final boolean bypass;
  public OpenMenuAction(ActionBuilder.Input input) {
    super(input);
    this.bypass = !input.option.isEmpty() && Boolean.parseBoolean(input.option);
  }

  @Override
  public void accept(UUID uuid, TaskProcess process) {
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
        runnable = () -> getInstance().getMenuManager().openMenu(menu, player, finalArgs, parentMenu, bypass);
      } else {
        runnable = () -> getInstance().getMenuManager().openMenu(menu, player, finalArgs, bypass);
      }
      Scheduler.current().sync().runEntityTaskWithFinalizer(player, runnable, process::next);
    } else {
      MessageUtils.sendMessage(player, getInstance().getMessageConfig().getMenuNotFound());
      process.next();
    }
  }
}
