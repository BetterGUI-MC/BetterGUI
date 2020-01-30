package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.ArrayList;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.util.CommonUtils;
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
      getInstance().getMainConfig().reloadConfig();
      getInstance().getMessageConfig().reloadConfig();
      getInstance().getItemsConfig().reloadConfig();
      getInstance().getAddonManager().reloadAddons();
      getInstance().checkClass();
      getInstance().getItemsConfig().initializeMenu();
      getInstance().loadMenuConfig();
      CommonUtils.sendMessage(commandSender, getInstance().getMessageConfig().get(DefaultMessage.SUCCESS));
      return true;
    } else {
      CommonUtils.sendMessage(commandSender, getInstance().getMessageConfig().get(DefaultMessage.NO_PERMISSION));
      return false;
    }
  }
}
