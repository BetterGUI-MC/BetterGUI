package me.hsgamer.bettergui.command;

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

import java.util.Arrays;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.MessageConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class AddonDownloaderCommand extends BukkitCommand {

  public AddonDownloaderCommand() {
    super("addondownloader", "Open the addon downloader", "/addondownloader",
        Arrays.asList("addondl", "addondown"));
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    if (!commandSender.hasPermission(Permissions.ADDON_DOWNLOADER)) {
      sendMessage(commandSender, MessageConfig.NO_PERMISSION.getValue());
      return false;
    }

    if (!(commandSender instanceof Player)) {
      sendMessage(commandSender, MessageConfig.PLAYER_ONLY.getValue());
      return false;
    }

    BetterGUI.getInstance().getAddonDownloader().openMenu((Player) commandSender);
    return true;
  }
}
