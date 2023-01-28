package me.hsgamer.bettergui.action.type;

import me.hsgamer.bettergui.builder.ActionBuilder;
import org.bukkit.entity.Player;

public class PlayerAction extends CommandAction {
  public PlayerAction(ActionBuilder.Input input) {
    super(input);
  }

  @Override
  protected void accept(Player player, String command) {
    player.chat(command);
  }
}
