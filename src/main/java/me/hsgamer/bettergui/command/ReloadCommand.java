package me.hsgamer.bettergui.command;

import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.manager.PluginVariableManager;
import me.hsgamer.hscore.bukkit.command.CommandManager;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Arrays;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class ReloadCommand extends BukkitCommand {
  public ReloadCommand() {
    super("reloadmenu", "Reload the plugin", "/reloadmenu", Arrays.asList("rlmenu", "reloadplugin", "rlplugin"));
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    if (!sender.hasPermission(Permissions.RELOAD)) {
      MessageUtils.sendMessage(sender, MessageConfig.NO_PERMISSION.getValue());
      return false;
    }

    getInstance().getMenuCommandManager().clearMenuCommand();
    PluginVariableManager.unregisterAll();
    getInstance().getMenuManager().clear();
    getInstance().getMainConfig().reloadConfig();
    getInstance().getMessageConfig().reloadConfig();
    getInstance().getTemplateButtonConfig().reloadConfig();
    if (commandLabel.equalsIgnoreCase("reloadplugin") || commandLabel.equalsIgnoreCase("rlplugin")) {
      getInstance().getAddonManager().callReload();
    }
    getInstance().loadMenuConfig();
    CommandManager.syncCommand();
    MessageUtils.sendMessage(sender, MessageConfig.SUCCESS.getValue());
    return true;
  }
}
