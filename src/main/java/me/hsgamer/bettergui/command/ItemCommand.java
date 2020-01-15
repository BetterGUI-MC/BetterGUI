package me.hsgamer.bettergui.command;

import java.util.ArrayList;
import me.hsgamer.bettergui.BetterGUI;
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
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    if (sender instanceof Player) {
      if (sender.hasPermission("yagui.items")) {
        BetterGUI.getInstance().getItemsConfig().createMenu((Player) sender);
        return true;
      } else {
        return false;
      }
    } else {
      sender.sendMessage(CommonUtils.colorize("&cYou should be a player to do this"));
      return false;
    }
  }
}
