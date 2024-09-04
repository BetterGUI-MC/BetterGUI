package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.action.common.Action;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BackAction implements Action {
  private final Menu menu;
  private final String value;
  private final boolean bypass;

  public BackAction(ActionBuilder.Input input) {
    this.menu = input.getMenu();
    this.value = input.getValue();
    this.bypass = input.getOption().equalsIgnoreCase("bypassChecks");
  }

  @Override
  public void apply(UUID uuid, TaskProcess process, StringReplacer stringReplacer) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      process.next();
      return;
    }

    String[] args = !value.isEmpty()
      ? stringReplacer.replaceOrOriginal(value, uuid).split(" ")
      : new String[0];

    Runnable runnable = menu.getParentMenu(uuid)
      .<Runnable>map(parentMenu -> () -> parentMenu.create(player, args, bypass))
      .orElse(player::closeInventory);
    SchedulerUtil.entity(player)
      .run(() -> {
        try {
          runnable.run();
        } finally {
          process.next();
        }
      }, process::next);
  }
}
