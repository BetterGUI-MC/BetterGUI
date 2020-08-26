package me.hsgamer.bettergui.command;

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

import java.util.Arrays;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.object.addon.AdditionalAddonSettings;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public final class GetAddonsCommand extends BukkitCommand {

  public GetAddonsCommand() {
    super("addons", "Get the loaded addons", "/addons",
        Arrays.asList("menuaddons", "getmenuaddons"));
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    if (!sender.hasPermission(Permissions.ADDONS)) {
      sendMessage(sender, MessageConfig.NO_PERMISSION.getValue());
      return false;
    }

    boolean shortMessage = args.length > 0 && args[0].equalsIgnoreCase("short");

    sendMessage(sender, "&b&lLoaded Addons:");
    BetterGUI.getInstance().getAddonManager().getLoadedAddons().forEach((name, addon) -> {
      sendMessage(sender, "  &f- &a" + name);
      if (!shortMessage) {
        sendMessage(sender, "    &eVersion: &f" + addon.getDescription().getVersion());
        sendMessage(sender, "    &eAuthors: &f" + AdditionalAddonSettings.AUTHORS.get(addon));
        sendMessage(sender,
            "    &eDescription: &f" + AdditionalAddonSettings.DESCRIPTION.get(addon));
      }
    });
    return true;
  }
}
