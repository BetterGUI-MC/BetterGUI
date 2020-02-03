package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.Arrays;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class GetAddonsCommand extends BukkitCommand {

  public GetAddonsCommand() {
    super("addons", "Get the loaded addons", "addons",
        Arrays.asList("menuaddons", "getmenuaddons"));
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    if (sender.hasPermission(Permissions.ADDONS)) {
      CommonUtils.sendMessage(sender, "&b&lLoaded Addons: &c" + String
          .join(", ", getInstance().getAddonManager().getLoadedAddons()));
      return true;
    } else {
      CommonUtils.sendMessage(sender,
          getInstance().getMessageConfig().get(DefaultMessage.NO_PERMISSION));
      return false;
    }
  }
}
