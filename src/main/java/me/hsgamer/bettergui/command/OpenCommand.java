package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.Arrays;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.manager.MenuManager;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class OpenCommand extends BukkitCommand {

  public OpenCommand() {
    super("openmenu", "Open the specific menu", "openmenu <menu_name>",
        Arrays.asList("menu", "om"));
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    if (commandSender instanceof Player) {
      if (commandSender.hasPermission(Permissions.OPEN_MENU)) {
        MenuManager menuManager = getInstance().getMenuManager();
        if (strings.length >= 1) {
          if (!menuManager.contains(strings[0])) {
            menuManager.openMenu(strings[0], (Player) commandSender);
            return true;
          } else {
            commandSender.sendMessage(CommonUtils.colorize("&cThat menu does not exist"));
            return false;
          }
        } else {
          commandSender.sendMessage(CommonUtils.colorize("&cYou should specify a menu"));
          return false;
        }
      } else {
        return false;
      }
    } else {
      commandSender.sendMessage(CommonUtils.colorize("&cYou should be a player to do this"));
      return false;
    }
  }
}
