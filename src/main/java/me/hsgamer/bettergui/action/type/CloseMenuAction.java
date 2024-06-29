package me.hsgamer.bettergui.action.type;

import io.github.projectunified.minelib.scheduler.entity.EntityScheduler;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.action.common.Action;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CloseMenuAction implements Action {
  private final Menu menu;

  public CloseMenuAction(Menu menu) {
    this.menu = menu;
  }

  @Override
  public void apply(UUID uuid, TaskProcess process, StringReplacer stringReplacer) {
    Player player = Bukkit.getPlayer(uuid);
    if (menu == null || player == null) {
      process.next();
      return;
    }
    EntityScheduler.get(BetterGUI.getInstance(), player)
      .run(() -> {
        try {
          menu.close(player);
        } finally {
          process.next();
        }
      }, process::next);
  }
}
