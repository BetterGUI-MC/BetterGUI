package me.hsgamer.bettergui.menu;

import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.downloader.AdditionalInfoKeys;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.button.ButtonMap;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.downloader.core.object.DownloadInfo;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class AddonMenu extends BaseInventoryMenu<ButtonMap> {
  private String upToDateMessage = "&cIt's already up-to-date";
  private String downloadingMessage = "&eDownloading {addon}";
  private String stillDownloadingMessage = "&cIt's still downloading";
  private String downloadFinishedMessage = "&aDownload finished";

  private String upToDateStatus = "&aUp-to-date";
  private String availableStatus = "&aAvailable";
  private String outdatedStatus = "&cOutdated";

  public AddonMenu(Config config) {
    super(config);
  }

  @Override
  protected ButtonMap createButtonMap(Config config) {
    upToDateMessage = Objects.toString(config.get("message.up-to-date"), upToDateMessage);
    downloadingMessage = Objects.toString(config.get("message.downloading"), downloadingMessage);
    stillDownloadingMessage = Objects.toString(config.get("message.still-downloading"), stillDownloadingMessage);
    downloadFinishedMessage = Objects.toString(config.get("message.download-finished"), downloadFinishedMessage);

    upToDateStatus = Objects.toString(config.get("status.up-to-date"), upToDateStatus);
    availableStatus = Objects.toString(config.get("status.available"), availableStatus);
    outdatedStatus = Objects.toString(config.get("status.outdated"), outdatedStatus);

    Map<String, Object> itemMap = config.getNormalizedValues("button", false);
    return uuid -> {
      Collection<DownloadInfo> downloadInfos = getInstance().getAddonDownloader().getLoadedDownloadInfo().values();
      Map<Button, List<Integer>> buttonMap = new HashMap<>();
      int slot = 0;
      for (DownloadInfo info : downloadInfos) {
        ItemBuilder itemBuilder = new ItemBuilder();
        ItemModifierBuilder.INSTANCE.build(itemMap).forEach(itemBuilder::addItemModifier);
        buttonMap.put(new AddonButton(info, itemBuilder), Collections.singletonList(slot++));
      }
      return buttonMap;
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

    @Override
    public ItemStack getItemStack(UUID uuid) {
      return itemBuilder.build(uuid);
    }

    @Override
    public void handleAction(UUID uuid, InventoryClickEvent event) {
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
      status = Optional.ofNullable(getInstance().getAddonManager().getAddon(downloadInfo.getName()))
        .map(addon -> addon.getDescription().getVersion().equals(downloadInfo.getVersion()) ? upToDateStatus : outdatedStatus)
        .orElse(availableStatus);
    }
  }
}
