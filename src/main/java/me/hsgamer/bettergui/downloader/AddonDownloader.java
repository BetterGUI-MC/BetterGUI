package me.hsgamer.bettergui.downloader;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.hscore.bukkit.gui.GUIHolder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.downloader.Downloader;
import me.hsgamer.hscore.downloader.object.DownloadInfo;

import java.util.UUID;

public class AddonDownloader extends Downloader {
  private final GUIHolder guiHolder;

  public AddonDownloader(BetterGUI instance) {
    super("https://raw.githubusercontent.com/BetterGUI-MC/Addon-List/master/addons.json", instance.getAddonManager().getAddonsDir());
    this.guiHolder = new GUIHolder(instance);
    loadDownloadsInfo();
  }

  public void createMenu() {
    guiHolder.setSize(54);
    guiHolder.setTitle(MessageUtils.colorize("&4&lAddon Downloader"));

    int i = 0;
    for (DownloadInfo downloadInfo : downloadInfoMap.values()) {
      AddonButton addonButton = new AddonButton(downloadInfo);
      addonButton.init();
      guiHolder.setButton(i++, addonButton);
    }
    guiHolder.init();
  }

  public void stopMenu() {
    guiHolder.stop();
    downloadInfoMap.clear();
  }

  public void openMenu(UUID uuid) {
    guiHolder.createDisplay(uuid).init();
  }
}
