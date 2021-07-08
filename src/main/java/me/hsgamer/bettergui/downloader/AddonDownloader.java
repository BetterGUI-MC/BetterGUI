package me.hsgamer.bettergui.downloader;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.hscore.bukkit.gui.simple.SimpleGUIBuilder;
import me.hsgamer.hscore.bukkit.gui.simple.SimpleGUIHolder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.downloader.Downloader;
import me.hsgamer.hscore.downloader.object.DownloadInfo;

import java.util.UUID;

public class AddonDownloader extends Downloader {
  private final SimpleGUIHolder guiHolder;

  public AddonDownloader(BetterGUI instance) {
    super("https://raw.githubusercontent.com/BetterGUI-MC/Addon-List/master/addons.json", instance.getAddonManager().getAddonsDir());
    this.guiHolder = new SimpleGUIHolder(instance);
    loadDownloadsInfo();
  }

  public void createMenu() {
    guiHolder.setSize(54);
    guiHolder.setTitle(MessageUtils.colorize("&4&lAddon Downloader"));
    guiHolder.init();
    checkDownloadInfo();
  }

  public void stopMenu() {
    guiHolder.stop();
    downloadInfoMap.clear();
  }

  public void openMenu(UUID uuid) {
    checkDownloadInfo();
    guiHolder.createDisplay(uuid).init();
  }

  private void checkDownloadInfo() {
    if (downloadInfoMap.isEmpty()) {
      return;
    }
    SimpleGUIBuilder builder = SimpleGUIBuilder.create(guiHolder);
    for (DownloadInfo downloadInfo : downloadInfoMap.values()) {
      AddonButton addonButton = new AddonButton(downloadInfo);
      addonButton.init();
      builder.add(addonButton);
    }
  }
}
