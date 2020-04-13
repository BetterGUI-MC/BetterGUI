package me.hsgamer.bettergui.object.command;

import co.aikar.taskchain.TaskChain;
import java.util.Optional;
import me.hsgamer.bettergui.Permissions;
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
    Object object = getVariableManager().getParent();
    if (object instanceof Icon) {
      Optional<Menu<?>> parentMenu = ((Icon) object).getMenu().getParentMenu();
      if (parentMenu.isPresent()) {
        String[] args = getParsedCommand(player).trim().split(" ");
        parentMenu.get()
            .createInventory(player, args, player.hasPermission(Permissions.OPEN_MENU_BYPASS));
      } else {
        player.closeInventory();
      }
    } else {
      player.closeInventory();
    }
  }
}
