package me.hsgamer.bettergui.downloader;

import io.github.projectunified.minelib.plugin.postenable.PostEnable;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.manager.AddonManager;
import me.hsgamer.hscore.downloader.core.Downloader;
import me.hsgamer.hscore.downloader.core.object.DownloadInfo;
import me.hsgamer.hscore.downloader.json.JsonDownloadInfoLoader;
import me.hsgamer.hscore.downloader.webstream.WebInputStreamLoader;

public class AddonDownloader implements PostEnable {
  private final BetterGUI plugin;
  private Downloader downloader;

  public AddonDownloader(BetterGUI plugin) {
    this.plugin = plugin;
  }

  @Override
  public void postEnable() {
    AddonManager addonManager = plugin.get(AddonManager.class);
    this.downloader = new Downloader(
      new JsonDownloadInfoLoader("https://bettergui-mc.github.io/Addon-List/addons.json"),
      new WebInputStreamLoader(),
      addonManager.getExpansionsDir()
    ) {
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
    };
    this.downloader.setup();
  }

  public Downloader getDownloader() {
    if (downloader == null) {
      throw new IllegalStateException("The downloader is not initialized yet");
    }
    return downloader;
  }
}
