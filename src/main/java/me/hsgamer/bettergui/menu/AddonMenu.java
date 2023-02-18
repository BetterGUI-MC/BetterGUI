package me.hsgamer.bettergui.menu;

import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.downloader.AdditionalInfoKeys;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.gui.event.BukkitClickEvent;
import me.hsgamer.hscore.bukkit.gui.object.BukkitItem;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.downloader.core.object.DownloadInfo;
import me.hsgamer.hscore.minecraft.gui.button.Button;
import me.hsgamer.hscore.minecraft.gui.button.ButtonMap;
import me.hsgamer.hscore.minecraft.gui.event.ClickEvent;
import me.hsgamer.hscore.ui.property.Initializable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class AddonMenu extends BaseInventoryMenu<ButtonMap> {
  private final String upToDateMessage;
  private final String downloadingMessage;
  private final String stillDownloadingMessage;
  private final String downloadFinishedMessage;
  private final String upToDateStatus;
  private final String availableStatus;
  private final String outdatedStatus;

  public AddonMenu(Config config) {
    super(config);
    upToDateMessage = Objects.toString(config.get("message.up-to-date"), "&cIt's already up-to-date");
    downloadingMessage = Objects.toString(config.get("message.downloading"), "&eDownloading {addon}");
    stillDownloadingMessage = Objects.toString(config.get("message.still-downloading"), "&cIt's still downloading");
    downloadFinishedMessage = Objects.toString(config.get("message.download-finished"), "&aDownload finished");
    upToDateStatus = Objects.toString(config.get("status.up-to-date"), "&aUp-to-date");
    availableStatus = Objects.toString(config.get("status.available"), "&aAvailable");
    outdatedStatus = Objects.toString(config.get("status.outdated"), "&cOutdated");
  }

  @Override
  protected ButtonMap createButtonMap(Config config) {
    Map<String, Object> itemMap = config.getNormalizedValues("button", false);
    return new ButtonMap() {
      private final Object lock = new Object();
      private Map<Button, Collection<Integer>> buttonMap;

      @Override
      public @NotNull Map<Button, Collection<Integer>> getButtons(@NotNull UUID uuid) {
        synchronized (lock) {
          if (buttonMap == null) {
            buttonMap = new HashMap<>();
            Collection<DownloadInfo> downloadInfos = getInstance().getAddonDownloader().getLoadedDownloadInfo().values();
            int slot = 0;
            for (DownloadInfo info : downloadInfos) {
              ItemBuilder itemBuilder = new ItemBuilder();
              ItemModifierBuilder.INSTANCE.build(itemMap).forEach(itemBuilder::addItemModifier);
              buttonMap.put(new AddonButton(info, itemBuilder), Collections.singletonList(slot++));
            }
            buttonMap.keySet().forEach(Initializable::init);
          }
        }
        return buttonMap;
      }

      @Override
      public void stop() {
        if (buttonMap != null) {
          buttonMap.keySet().forEach(Initializable::stop);
          buttonMap.clear();
        }
      }
    };
  }

  private class AddonButton implements Button {
    private final DownloadInfo downloadInfo;
    private final ItemBuilder itemBuilder;
    private String status = "";

    AddonButton(DownloadInfo downloadInfo, ItemBuilder itemBuilder) {
      this.downloadInfo = downloadInfo;
      this.itemBuilder = itemBuilder;
      itemBuilder.addStringReplacer("download-info", (original, uuid) -> original
        .replace("{status}", status)
        .replace("{name}", downloadInfo.getName())
        .replace("{version}", downloadInfo.getVersion())
        .replace("{description}", AdditionalInfoKeys.DESCRIPTION.get(downloadInfo))
        .replace("{author}", AdditionalInfoKeys.AUTHORS.get(downloadInfo).toString())
      );
      itemBuilder.addStringReplacer("colorize", StringReplacerApplier.COLORIZE);
    }

    private void updateStatus() {
      status = getInstance().getAddonManager().getExpansionClassLoader(downloadInfo.getName())
        .map(addon -> addon.getDescription().getVersion().equals(downloadInfo.getVersion()) ? upToDateStatus : outdatedStatus)
        .orElse(availableStatus);
    }

    @Override
    public BukkitItem getItem(@NotNull UUID uuid) {
      updateStatus();
      return new BukkitItem(itemBuilder.build(uuid));
    }

    @Override
    public void handleAction(@NotNull ClickEvent clickEvent) {
      if (!(clickEvent instanceof BukkitClickEvent)) return;
      InventoryClickEvent event = ((BukkitClickEvent) clickEvent).getEvent();
      HumanEntity humanEntity = event.getWhoClicked();
      ClickType clickType = event.getClick();
      if (clickType.isLeftClick()) {
        if (status.equals(upToDateStatus)) {
          MessageUtils.sendMessage(humanEntity, upToDateMessage);
          return;
        }

        if (downloadInfo.isDownloading()) {
          MessageUtils.sendMessage(humanEntity, stillDownloadingMessage);
        } else {
          MessageUtils.sendMessage(humanEntity, downloadingMessage.replace("{addon}", downloadInfo.getName()));
          downloadInfo.download().whenComplete((file, throwable) -> {
            if (throwable != null) {
              getInstance().getLogger().log(Level.WARNING, throwable, () -> "Unexpected issue when downloading " + downloadInfo.getName());
              MessageUtils.sendMessage(humanEntity, "&cAn unexpected issue occurs when downloading. Check the console");
              return;
            }
            MessageUtils.sendMessage(humanEntity, downloadFinishedMessage);
          });
          MessageUtils.sendMessage(humanEntity, getInstance().getMessageConfig().success);
        }
      } else if (clickType.isRightClick()) {
        MessageUtils.sendMessage(humanEntity, "&bLink: &f" + AdditionalInfoKeys.SOURCE_CODE.get(downloadInfo));
      } else if (clickType.equals(ClickType.MIDDLE)) {
        MessageUtils.sendMessage(humanEntity, "&bLink: &f" + AdditionalInfoKeys.WIKI.get(downloadInfo));
      }
    }

    @Override
    public void init() {
      updateStatus();
    }
  }
}
