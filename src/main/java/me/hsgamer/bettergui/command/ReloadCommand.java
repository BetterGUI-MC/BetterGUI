package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.Arrays;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.TestCase;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class ReloadCommand extends BukkitCommand {

  public ReloadCommand() {
    super("reloadmenu", "Reload the plugin", "/reloadmenu",
        Arrays.asList("rlmenu", "reloadplugin", "rlplugin"));
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    return TestCase.create(commandSender)
        .setPredicate(commandSender1 -> commandSender1.hasPermission(Permissions.RELOAD))
        .setSuccessConsumer(commandSender1 -> {
          getInstance().getCommandManager().clearMenuCommand();
          getInstance().getMenuManager().clear();
          getInstance().getMainConfig().reloadConfig();
          getInstance().getMessageConfig().reloadConfig();
          if (s.equalsIgnoreCase("reloadplugin") || s.equalsIgnoreCase("rlplugin")) {
            getInstance().getAddonManager().reloadAddons();
          }
          getInstance().checkClass();
          getInstance().loadMenuConfig();
          CommonUtils
              .sendMessage(commandSender1,
                  getInstance().getMessageConfig().get(DefaultMessage.SUCCESS));
        })
        .setFailConsumer(commandSender1 -> CommonUtils.sendMessage(commandSender,
            getInstance().getMessageConfig().get(DefaultMessage.NO_PERMISSION)))
        .test();
  }
}
