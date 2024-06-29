package me.hsgamer.bettergui.downloader;

import io.github.projectunified.minelib.plugin.postenable.PostEnable;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.manager.AddonManager;
import me.hsgamer.hscore.downloader.core.Downloader;
import me.hsgamer.hscore.downloader.core.object.DownloadInfo;
import me.hsgamer.hscore.downloader.json.JsonDownloadInfoLoader;
import me.hsgamer.hscore.downloader.webstream.WebInputStreamLoader;

public class AddonDownloader extends Downloader implements PostEnable {
  private final BetterGUI plugin;
  private final AddonManager addonManager;

  public AddonDownloader(BetterGUI plugin, AddonManager addonManager) {
    super(
      new JsonDownloadInfoLoader("https://raw.githubusercontent.com/BetterGUI-MC/Addon-List/master/addons.json"),
      new WebInputStreamLoader(),
      addonManager.getExpansionsDir()
    );
    this.plugin = plugin;
    this.addonManager = addonManager;
  }

  @Override
  public void onLoaded() {
    for (DownloadInfo downloadInfo : getLoadedDownloadInfo().values()) {
      addonManager.getExpansionClassLoader(downloadInfo.getName()).ifPresent(loader -> {
        if (!loader.getDescription().getVersion().equals(downloadInfo.getVersion())) {
          plugin.getLogger().warning(() -> "The addon '" + downloadInfo.getName() + "' has a new update. New Version: " + downloadInfo.getVersion());
        }
      });
    }
  }

  @Override
  public void postEnable() {
    setup();
  }
}
