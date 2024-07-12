package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.manager.MenuManager;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.action.common.Action;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class OpenMenuAction implements Action {
  private final Menu menu;
  private final String value;
  private final boolean bypass;

  public OpenMenuAction(ActionBuilder.Input input) {
    this.menu = input.getMenu();
    this.value = input.getValue();
    this.bypass = input.getOption().equalsIgnoreCase("bypassChecks");
  }

  @Override
  public void apply(UUID uuid, TaskProcess process, StringReplacer stringReplacer) {
    // Get Menu and Arguments
    String[] split = stringReplacer.replaceOrOriginal(value, uuid).split(" ");
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
    if (getInstance().get(MenuManager.class).contains(menu)) {
      String[] finalArgs = args;
      Menu parentMenu = this.menu;
      SchedulerUtil.entity(player)
        .run(() -> {
          try {
            getInstance().get(MenuManager.class).openMenu(menu, player, finalArgs, parentMenu, bypass);
          } finally {
            process.next();
          }
        }, process::next);
    } else {
      BetterGUI betterGUI = getInstance();
      MessageUtils.sendMessage(player, betterGUI.get(MessageConfig.class).getMenuNotFound());
      process.next();
    }
  }
}
