package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.action.common.Action;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UpdateMenuAction implements Action {
  private final Menu menu;

  public UpdateMenuAction(Menu menu) {
    this.menu = menu;
  }

  @Override
  public void apply(UUID uuid, TaskProcess process, StringReplacer stringReplacer) {
    Player player = Bukkit.getPlayer(uuid);
    if (menu == null || player == null) {
      process.next();
      return;
    }
    SchedulerUtil.entity(player)
      .run(() -> {
        try {
          menu.update(player);
        } finally {
          process.next();
        }
      }, process::next);
  }
}
