package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.ArrayList;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class ItemCommand extends BukkitCommand {

  public ItemCommand() {
    super("items", "Open the inventory that contains all items from items.yml", "items",
        new ArrayList<>());
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    if (commandSender instanceof Player) {
      if (commandSender.hasPermission(Permissions.ITEMS)) {
        getInstance().getItemsConfig().createMenu((Player) commandSender);
        return true;
      } else {
        return false;
      }
    } else {
      commandSender.sendMessage(CommonUtils.colorize("&cYou should be a player to do this"));
      return false;
    }
  }
}
