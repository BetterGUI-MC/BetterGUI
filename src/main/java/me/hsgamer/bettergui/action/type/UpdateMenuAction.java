package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UpdateMenuAction implements Action {
  private final Menu menu;

  public UpdateMenuAction(Menu menu) {
    this.menu = menu;
  }

  @Override
  public void accept(UUID uuid, BatchRunnable.Process process) {
    Player player = Bukkit.getPlayer(uuid);
    if (menu == null || player == null) {
      process.next();
      return;
    }
    Bukkit.getScheduler().runTask(BetterGUI.getInstance(), () -> {
      menu.update(player);
      process.next();
    });
  }

  @Override
  public Menu getMenu() {
    return menu;
  }
}
