package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.Collections;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.manager.MenuManager;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class OpenCommand extends BukkitCommand {

  public OpenCommand() {
    super("openmenu", "Open the specific menu", "openmenu <menu_name> [player]",
        Collections.singletonList("om"));
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    if (commandSender instanceof Player) {
      if (commandSender.hasPermission(Permissions.OPEN_MENU)) {
        MenuManager menuManager = getInstance().getMenuManager();
        if (strings.length > 0) {
          if (menuManager.contains(strings[0])) {
            if (strings.length > 1) {
              Player player = Bukkit.getPlayer(strings[1]);
              if (player != null && player.isOnline()) {
                menuManager.openMenu(strings[0], player);
                return true;
              } else {
                CommonUtils.sendMessage(commandSender,
                    getInstance().getMessageConfig().get(DefaultMessage.PLAYER_NOT_FOUND));
                return false;
              }
            } else {
              menuManager.openMenu(strings[0], (Player) commandSender);
              return true;
            }
          } else {
            CommonUtils.sendMessage(commandSender,
                getInstance().getMessageConfig().get(DefaultMessage.MENU_NOT_FOUND));
            return false;
          }
        } else {
          CommonUtils.sendMessage(commandSender,
              getInstance().getMessageConfig().get(DefaultMessage.MENU_REQUIRED));
          return false;
        }
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
