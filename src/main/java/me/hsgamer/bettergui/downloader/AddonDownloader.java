package me.hsgamer.bettergui.downloader;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.hscore.downloader.core.Downloader;
import me.hsgamer.hscore.downloader.json.JsonDownloadInfoLoader;
import me.hsgamer.hscore.downloader.webstream.WebInputStreamLoader;

public class AddonDownloader extends Downloader {
  public AddonDownloader(BetterGUI plugin) {
    super(
      new JsonDownloadInfoLoader("https://raw.githubusercontent.com/BetterGUI-MC/Addon-List/master/addons.json"),
      new WebInputStreamLoader(),
      plugin.getAddonDownloader().getFolder()
    );
  }
}
