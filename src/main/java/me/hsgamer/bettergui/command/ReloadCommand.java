package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.Arrays;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.impl.MessageConfig;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public final class ReloadCommand extends BukkitCommand {

  public ReloadCommand() {
    super("reloadmenu", "Reload the plugin", "/reloadmenu",
        Arrays.asList("rlmenu", "reloadplugin", "rlplugin"));
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    if (!commandSender.hasPermission(Permissions.RELOAD)) {
      CommonUtils.sendMessage(commandSender, MessageConfig.NO_PERMISSION.getValue());
      return false;
    }

    getInstance().getCommandManager().clearMenuCommand();
    getInstance().getMenuManager().clear();
    getInstance().getMainConfig().reloadConfig();
    getInstance().getMessageConfig().reloadConfig();
    if (s.equalsIgnoreCase("reloadplugin") || s.equalsIgnoreCase("rlplugin")) {
      getInstance().getAddonManager().callReload();
    }
    getInstance().loadMenuConfig();
    getInstance().getCommandManager().syncCommand();
    CommonUtils.sendMessage(commandSender, MessageConfig.SUCCESS.getValue());
    return true;
  }
}
