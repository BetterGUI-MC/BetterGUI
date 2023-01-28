package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.util.CommandUtil;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * The command action
 */
public abstract class CommandAction extends BaseAction {
  /**
   * Create a new action
   *
   * @param input the input
   */
  protected CommandAction(ActionBuilder.Input input) {
    super(input);
  }

  /**
   * Accept the command
   *
   * @param player  the player
   * @param command the command
   */
  protected abstract void accept(Player player, String command);

  @Override
  public void accept(UUID uuid, TaskProcess process) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      process.next();
      return;
    }

    String command = CommandUtil.normalizeCommand(getReplacedString(uuid));
    Bukkit.getScheduler().runTask(BetterGUI.getInstance(), () -> {
      accept(player, command);
      process.next();
    });
  }
}