package me.hsgamer.bettergui.action.type;

import io.github.projectunified.minelib.scheduler.entity.EntityScheduler;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.action.common.Action;
import me.hsgamer.hscore.common.StringReplacer;
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
  public void apply(UUID uuid, TaskProcess process, StringReplacer stringReplacer) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      process.next();
      return;
    }

    Runnable runnable = menu.getParentMenu(uuid)
      .<Runnable>map(parentMenu -> () -> parentMenu.create(player, new String[0], player.hasPermission(Permissions.OPEN_MENU_BYPASS)))
      .orElse(player::closeInventory);
    EntityScheduler.get(BetterGUI.getInstance(), player)
      .run(() -> {
        try {
          runnable.run();
        } finally {
          process.next();
        }
      }, process::next);
  }
}
