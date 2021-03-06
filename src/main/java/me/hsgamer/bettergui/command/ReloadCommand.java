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
    setPermission(Permissions.RELOAD.getName());
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    if (!testPermission(sender)) {
      return false;
    }

    getInstance().getMenuCommandManager().clearMenuCommand();
    getInstance().getMenuManager().clear();
    PluginVariableManager.unregisterAll();
    getInstance().getMainConfig().reload();
    getInstance().getMessageConfig().reload();
    getInstance().getTemplateButtonConfig().reload();
    PluginVariableManager.registerDefaultVariables();
    if (commandLabel.equalsIgnoreCase("reloadplugin") || commandLabel.equalsIgnoreCase("rlplugin")) {
      getInstance().getAddonManager().callReload();
    }
    getInstance().getMenuManager().loadMenuConfig();
    CommandManager.syncCommand();
    MessageUtils.sendMessage(sender, MessageConfig.SUCCESS.getValue());
    return true;
  }
}
