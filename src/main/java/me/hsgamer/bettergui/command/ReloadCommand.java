package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.ArrayList;
import me.hsgamer.bettergui.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class ReloadCommand extends BukkitCommand {

  public ReloadCommand() {
    super("reloadmenu", "Reload the plugin", "reloadmenu", new ArrayList<>());
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    if (commandSender.hasPermission(Permissions.RELOAD)) {
      getInstance().getCommandManager().clearMenuCommand();
      getInstance().getMenuManager().clear();
      getInstance().getAddonManager().reloadAddons();
      getInstance().checkClass();
      getInstance().getItemsConfig().reloadConfig();
      getInstance().loadMenuConfig();
      return true;
    } else {
      return false;
    }
  }
}
