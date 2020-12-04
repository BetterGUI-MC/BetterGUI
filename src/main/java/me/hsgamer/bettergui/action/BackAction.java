package me.hsgamer.bettergui.action;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.api.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class BackAction implements Action {
  private Menu menu;

  @Override
  public void addToTaskChain(UUID uuid, TaskChain<?> taskChain) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      return;
    }

    Runnable runnable = menu != null ? () -> menu.createInventory(player, new String[0], player.hasPermission(Permissions.OPEN_MENU_BYPASS)) : player::closeInventory;
    taskChain.sync(() -> getInstance().getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), runnable));
  }

  @Override
  public Menu getMenu() {
    return menu;
  }

  @Override
  public void setMenu(Menu menu) {
    this.menu = menu;
  }
}
