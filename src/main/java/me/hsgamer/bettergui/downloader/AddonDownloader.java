package me.hsgamer.bettergui.downloader;

import me.hsgamer.hscore.bukkit.gui.GUIHolder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.downloader.Downloader;
import me.hsgamer.hscore.downloader.object.DownloadInfo;

import java.util.UUID;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class AddonDownloader extends Downloader {
  private GUIHolder guiHolder;

  public AddonDownloader() {
    super("https://raw.githubusercontent.com/BetterGUI-MC/Addon-List/master/addons.json", getInstance().getAddonManager().getAddonsDir());
    loadDownloadsInfo();
  }

  public void createMenu() {
    guiHolder = new GUIHolder(getInstance());
    guiHolder.setSize(54);
    guiHolder.setTitle(MessageUtils.colorize("&4&lAddon Downloader"));

    int i = 0;
    for (DownloadInfo downloadInfo : downloadInfoMap.values()) {
      AddonButton addonButton = new AddonButton(downloadInfo);
      addonButton.init();
      guiHolder.setButton(i++, addonButton);
    }
  }

  public void stopMenu() {
    if (guiHolder != null) {
      guiHolder.stop();
    }
    downloadInfoMap.clear();
  }

  public void openMenu(UUID uuid) {
    if (guiHolder != null) {
      guiHolder.createDisplay(uuid).init();
    }
  }
}
