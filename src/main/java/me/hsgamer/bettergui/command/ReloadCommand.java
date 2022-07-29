package me.hsgamer.bettergui.command;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.manager.PluginVariableManager;
import me.hsgamer.hscore.bukkit.command.CommandManager;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Arrays;

public class ReloadCommand extends BukkitCommand {
  private final BetterGUI plugin;

  public ReloadCommand(BetterGUI plugin) {
    super("reloadmenu", "Reload the plugin", "/reloadmenu", Arrays.asList("rlmenu", "reloadplugin", "rlplugin"));
    this.plugin = plugin;
    setPermission(Permissions.RELOAD.getName());
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    if (!testPermission(sender)) {
      return false;
    }

    plugin.getMenuCommandManager().clearMenuCommand();
    plugin.getMenuManager().clear();
    PluginVariableManager.unregisterAll();
    plugin.getMainConfig().reload();
    plugin.getMessageConfig().reload();
    plugin.getTemplateButtonConfig().reload();
    PluginVariableManager.registerDefaultVariables();
    if (commandLabel.equalsIgnoreCase("reloadplugin") || commandLabel.equalsIgnoreCase("rlplugin")) {
      plugin.getAddonManager().callReload();
    }
    plugin.getMenuManager().loadMenuConfig();
    CommandManager.syncCommand();
    MessageUtils.sendMessage(sender, plugin.getMessageConfig().success);
    return true;
  }
}
