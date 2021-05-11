package me.hsgamer.bettergui.command;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.MessageConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

public class AddonDownloaderCommand extends BukkitCommand {

  public AddonDownloaderCommand() {
    super("addondownloader", "Open the addon downloader", "/addondownloader", Arrays.asList("addondl", "addondown"));
    setPermission(Permissions.ADDON_DOWNLOADER.getName());
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    if (!testPermission(commandSender)) {
      return false;
    }

    if (!(commandSender instanceof Player)) {
      sendMessage(commandSender, MessageConfig.PLAYER_ONLY.getValue());
      return false;
    }

    BetterGUI.getInstance().getAddonDownloader().openMenu(((Player) commandSender).getUniqueId());
    return true;
  }
}