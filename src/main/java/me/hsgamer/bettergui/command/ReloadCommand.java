package me.hsgamer.bettergui.command;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.api.addon.Reloadable;
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
    plugin.getTemplateButtonConfig().clear();
    plugin.getMainConfig().reloadConfig();
    plugin.getMessageConfig().reloadConfig();
    if (commandLabel.equalsIgnoreCase("reloadplugin") || commandLabel.equalsIgnoreCase("rlplugin")) {
      plugin.getAddonManager().call(Reloadable.class, Reloadable::onReload);
    }
    plugin.getTemplateButtonConfig().setIncludeMenuInTemplate(plugin.getMainConfig().isIncludeMenuInTemplate());
    plugin.getTemplateButtonConfig().setup();
    plugin.getMenuManager().loadMenuConfig();
    CommandManager.syncCommand();
    MessageUtils.sendMessage(sender, plugin.getMessageConfig().getSuccess());
    return true;
  }
}
