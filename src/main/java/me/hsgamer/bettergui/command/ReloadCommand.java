package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.Arrays;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
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
      CommonUtils.sendMessage(commandSender,
          getInstance().getMessageConfig().get(DefaultMessage.NO_PERMISSION));
      return false;
    }

    getInstance().getCommandManager().clearMenuCommand();
    getInstance().getMenuManager().clear();
    getInstance().getMainConfig().reloadConfig();
    getInstance().getMessageConfig().reloadConfig();
    if (s.equalsIgnoreCase("reloadplugin") || s.equalsIgnoreCase("rlplugin")) {
      getInstance().getAddonManager().reloadAddons();
    } else {
      getInstance().getAddonManager().callReload();
    }
    getInstance().checkClass();
    getInstance().loadMenuConfig();
    getInstance().getCommandManager().syncCommand();
    CommonUtils
        .sendMessage(commandSender, getInstance().getMessageConfig().get(DefaultMessage.SUCCESS));
    return true;
  }
}
