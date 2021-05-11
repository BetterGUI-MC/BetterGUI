package me.hsgamer.bettergui.command;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.manager.BetterGUIAddonManager.Setting;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Arrays;

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

public final class GetAddonsCommand extends BukkitCommand {

  public GetAddonsCommand() {
    super("addons", "Get the loaded addons", "/addons", Arrays.asList("menuaddons", "getmenuaddons"));
    setPermission(Permissions.ADDONS.getName());
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    if (!testPermission(sender)) {
      return false;
    }

    boolean shortMessage = args.length > 0 && args[0].equalsIgnoreCase("short");

    sendMessage(sender, "&b&lLoaded Addons:");
    BetterGUI.getInstance().getAddonManager().getLoadedAddons().forEach((name, addon) -> {
      sendMessage(sender, "  &f- &a" + name);
      if (!shortMessage) {
        sendMessage(sender, "    &eVersion: &f" + addon.getDescription().getVersion());
        sendMessage(sender, "    &eAuthors: &f" + Setting.AUTHORS.get(addon));
        sendMessage(sender, "    &eDescription: &f" + Setting.DESCRIPTION.get(addon));
      }
    });
    return true;
  }
}