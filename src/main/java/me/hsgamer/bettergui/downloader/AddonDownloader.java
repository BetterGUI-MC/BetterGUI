package me.hsgamer.bettergui.downloader;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.hscore.bukkit.gui.simple.SimpleGUIBuilder;
import me.hsgamer.hscore.bukkit.gui.simple.SimpleGUIHolder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.downloader.Downloader;
import me.hsgamer.hscore.downloader.object.DownloadInfo;

import java.util.UUID;
import java.util.logging.Level;

public class AddonDownloader extends Downloader {
  private final BetterGUI instance;
  private final SimpleGUIHolder guiHolder;

  public AddonDownloader(BetterGUI instance) {
    super("https://raw.githubusercontent.com/BetterGUI-MC/Addon-List/master/addons.json", instance.getAddonManager().getAddonsDir());
    this.instance = instance;
    this.guiHolder = new SimpleGUIHolder(instance);
  }

  public void setup() {
    this.loadDownloadsInfo()
      .whenComplete((map, throwable) -> {
        if (map == null) {
          if (throwable != null) {
            instance.getLogger().log(Level.WARNING, "Cannot load the addon information", throwable);
          }
          return;
        }
        SimpleGUIBuilder builder = SimpleGUIBuilder.create(guiHolder);
        for (DownloadInfo downloadInfo : downloadInfoMap.values()) {
          AddonButton addonButton = new AddonButton(downloadInfo);
          addonButton.init();
          builder.add(addonButton);
          if (addonButton.getStatus() == AddonButton.Status.OUTDATED) {
            instance.getLogger().warning(() -> "The addon '" + downloadInfo.getName() + "' has a new update. New Version: " + downloadInfo.getVersion());
          }
        }
      });
  }

  public void createMenu() {
    guiHolder.setSize(54);
    guiHolder.setTitle(MessageUtils.colorize("&4&lAddon Downloader"));
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
