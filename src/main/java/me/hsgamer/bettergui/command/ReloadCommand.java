package me.hsgamer.bettergui.command;

import io.github.projectunified.minelib.plugin.command.CommandComponent;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.api.addon.Reloadable;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.config.TemplateConfig;
import me.hsgamer.bettergui.manager.AddonManager;
import me.hsgamer.bettergui.manager.MenuCommandManager;
import me.hsgamer.bettergui.manager.MenuManager;
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

    plugin.get(MenuCommandManager.class).clearMenuCommand();
    plugin.get(MenuManager.class).clear();
    plugin.get(TemplateConfig.class).clear();
    plugin.get(MainConfig.class).reloadConfig();
    plugin.get(MessageConfig.class).reloadConfig();
    if (commandLabel.equalsIgnoreCase("reloadplugin") || commandLabel.equalsIgnoreCase("rlplugin")) {
      plugin.get(AddonManager.class).call(Reloadable.class, Reloadable::onReload);
    }
    plugin.get(TemplateConfig.class).setup();
    plugin.get(MenuManager.class).loadMenuConfig();
    CommandComponent.syncCommand();
    MessageUtils.sendMessage(sender, plugin.get(MessageConfig.class).getSuccess());
    return true;
  }
}
