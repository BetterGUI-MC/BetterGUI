package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import java.util.Optional;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import org.bukkit.entity.Player;

public class BackCommand extends Command {

  public BackCommand(String command) {
    super(command);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    Optional<Icon> icon = getIcon();
    if (icon.isPresent()) {
      Optional<Menu> parentMenu = icon.get().getMenu().getParentMenu();
      if (parentMenu.isPresent()) {
        parentMenu.get().createInventory(player);
      } else {
        player.closeInventory();
      }
    } else {
      player.closeInventory();
    }
  }
}
