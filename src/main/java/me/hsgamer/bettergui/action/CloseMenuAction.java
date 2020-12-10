package me.hsgamer.bettergui.action;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.api.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CloseMenuAction implements Action {
  private Menu menu;

  @Override
  public void addToTaskChain(UUID uuid, TaskChain<?> taskChain) {
    Player player = Bukkit.getPlayer(uuid);
    if (menu == null && player == null) {
      return;
    }
    taskChain.sync(() -> menu.closeInventory(player));
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
