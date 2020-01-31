package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.ArrayList;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
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
        CommonUtils.sendMessage(commandSender,
            getInstance().getMessageConfig().get(DefaultMessage.NO_PERMISSION));
        return false;
      }
    } else {
      CommonUtils.sendMessage(commandSender,
          getInstance().getMessageConfig().get(DefaultMessage.PLAYER_ONLY));
      return false;
    }
  }
}
