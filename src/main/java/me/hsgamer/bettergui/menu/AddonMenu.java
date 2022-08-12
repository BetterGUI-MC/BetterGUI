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
import me.hsgamer.hscore.ui.property.Initializable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
      private final Map<Button, List<Integer>> buttonMap = new HashMap<>();

      @Override
      public Map<Button, List<Integer>> getButtons(UUID uuid) {
        Collection<DownloadInfo> downloadInfos = getInstance().getAddonDownloader().getLoadedDownloadInfo().values();
        int slot = 0;
        for (DownloadInfo info : downloadInfos) {
          ItemBuilder itemBuilder = new ItemBuilder();
          ItemModifierBuilder.INSTANCE.build(itemMap).forEach(itemBuilder::addItemModifier);
          buttonMap.put(new AddonButton(info, itemBuilder), Collections.singletonList(slot++));
        }
        return buttonMap;
      }

      @Override
      public void init() {
        buttonMap.keySet().forEach(Initializable::init);
      }

      @Override
      public void stop() {
        buttonMap.keySet().forEach(Initializable::stop);
        buttonMap.clear();
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
