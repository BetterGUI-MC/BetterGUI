package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BackAction implements Action {
  private final Menu menu;

  public BackAction(Menu menu) {
    this.menu = menu;
  }

  @Override
  public void accept(UUID uuid, TaskProcess process) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      return;
    }

    Runnable runnable = menu.getParentMenu(uuid)
      .<Runnable>map(parentMenu -> () -> parentMenu.create(player, new String[0], player.hasPermission(Permissions.OPEN_MENU_BYPASS)))
      .orElse(player::closeInventory);
    Bukkit.getScheduler().runTask(BetterGUI.getInstance(), () -> {
      runnable.run();
      process.next();
    });
  }

  @Override
  public Menu getMenu() {
    return menu;
  }
}
