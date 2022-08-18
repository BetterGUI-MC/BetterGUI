package me.hsgamer.bettergui.downloader;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.hscore.addon.object.Addon;
import me.hsgamer.hscore.downloader.core.Downloader;
import me.hsgamer.hscore.downloader.core.object.DownloadInfo;
import me.hsgamer.hscore.downloader.json.JsonDownloadInfoLoader;
import me.hsgamer.hscore.downloader.webstream.WebInputStreamLoader;

public class AddonDownloader extends Downloader {
  private final BetterGUI plugin;

  public AddonDownloader(BetterGUI plugin) {
    super(
      new JsonDownloadInfoLoader("https://raw.githubusercontent.com/BetterGUI-MC/Addon-List/master/addons.json"),
      new WebInputStreamLoader(),
      plugin.getAddonManager().getAddonsDir()
    );
    this.plugin = plugin;
  }

  @Override
  public void onLoaded() {
    for (DownloadInfo downloadInfo : getLoadedDownloadInfo().values()) {
      Addon addon = plugin.getAddonManager().getAddon(downloadInfo.getName());
      if (addon == null) continue;
      if (!addon.getDescription().getVersion().equals(downloadInfo.getVersion())) {
        plugin.getLogger().warning(() -> "The addon '" + downloadInfo.getName() + "' has a new update. New Version: " + downloadInfo.getVersion());
      }
    }
  }
}
