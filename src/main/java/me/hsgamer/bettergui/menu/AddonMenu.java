package me.hsgamer.bettergui.menu;

import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.downloader.AdditionalInfoKeys;
import me.hsgamer.bettergui.util.CaseInsensitivePathString;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.gui.event.BukkitClickEvent;
import me.hsgamer.hscore.bukkit.gui.object.BukkitItem;
import me.hsgamer.hscore.bukkit.item.BukkitItemBuilder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathString;
import me.hsgamer.hscore.downloader.core.object.DownloadInfo;
import me.hsgamer.hscore.minecraft.gui.button.Button;
import me.hsgamer.hscore.minecraft.gui.button.ButtonMap;
import me.hsgamer.hscore.minecraft.gui.button.ViewedButton;
import me.hsgamer.hscore.minecraft.gui.event.ClickEvent;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
import me.hsgamer.hscore.ui.property.Initializable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class AddonMenu extends BaseInventoryMenu<ButtonMap> {
  private static final PathString MESSAGE_PATH = new PathString("message");
  private static final PathString STATUS_PATH = new PathString("status");
  private static final PathString BUTTON_PATH = new PathString("button");

  private final String upToDateMessage;
  private final String downloadingMessage;
  private final String stillDownloadingMessage;
  private final String downloadFinishedMessage;
  private final String upToDateStatus;
  private final String availableStatus;
  private final String outdatedStatus;

  public AddonMenu(Config config) {
    super(config);
    upToDateMessage = Objects.toString(config.get(MESSAGE_PATH.append("up-to-date"), "&cIt's already up-to-date"));
    downloadingMessage = Objects.toString(config.get(MESSAGE_PATH.append("downloading"), "&eDownloading {addon}"));
    stillDownloadingMessage = Objects.toString(config.get(MESSAGE_PATH.append("still-downloading"), "&cIt's still downloading"));
    downloadFinishedMessage = Objects.toString(config.get(MESSAGE_PATH.append("download-finished"), "&aDownload finished"));
    upToDateStatus = Objects.toString(config.get(STATUS_PATH.append("up-to-date"), "&aUp-to-date"));
    availableStatus = Objects.toString(config.get(STATUS_PATH.append("available"), "&aAvailable"));
    outdatedStatus = Objects.toString(config.get(STATUS_PATH.append("outdated"), "&cOutdated"));
  }

  @Override
  protected ButtonMap createButtonMap() {
    Object rawItemMap = configSettings.get(new CaseInsensitivePathString(BUTTON_PATH));
    if (!(rawItemMap instanceof Map)) {
      throw new IllegalStateException("The button map must be a map");
    }
    //noinspection unchecked
    Map<String, Object> itemMap = (Map<String, Object>) rawItemMap;

    return new ButtonMap() {
      private final Map<DownloadInfo, AddonButton> addonButtonMap = new HashMap<>();

      @Override
      public @NotNull Map<@NotNull Integer, @NotNull ViewedButton> getButtons(@NotNull UUID uuid, int size) {
        Map<Integer, ViewedButton> buttonMap = new HashMap<>();
        Collection<DownloadInfo> downloadInfos = getInstance().getAddonDownloader().getLoadedDownloadInfo().values();
        int slot = 0;
        for (DownloadInfo info : downloadInfos) {
          AddonButton button = addonButtonMap.computeIfAbsent(info, downloadInfo -> {
            ItemBuilder<ItemStack> itemBuilder = new BukkitItemBuilder();
            ItemModifierBuilder.INSTANCE.build(itemMap).forEach(itemBuilder::addItemModifier);
            AddonButton addonButton = new AddonButton(downloadInfo, itemBuilder);
            addonButton.init();
            return addonButton;
          });
          ViewedButton viewedButton = new ViewedButton();
          viewedButton.setButton(button);
          viewedButton.setDisplayItem(button.getItem(uuid));
          buttonMap.put(slot++, viewedButton);
        }
        return buttonMap;
      }

      @Override
      public void stop() {
        addonButtonMap.values().forEach(Initializable::stop);
        addonButtonMap.clear();
      }
    };
  }

  private class AddonButton implements Button {
    private final DownloadInfo downloadInfo;
    private final ItemBuilder<ItemStack> itemBuilder;
    private String status = "";

    AddonButton(DownloadInfo downloadInfo, ItemBuilder<ItemStack> itemBuilder) {
      this.downloadInfo = downloadInfo;
      this.itemBuilder = itemBuilder;
      itemBuilder.addStringReplacer(original -> original
        .replace("{status}", status)
        .replace("{name}", downloadInfo.getName())
        .replace("{version}", downloadInfo.getVersion())
        .replace("{description}", AdditionalInfoKeys.DESCRIPTION.get(downloadInfo))
        .replace("{author}", AdditionalInfoKeys.AUTHORS.get(downloadInfo).toString())
      );
      itemBuilder.addStringReplacer(StringReplacerApplier.COLORIZE);
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
          MessageUtils.sendMessage(humanEntity, getInstance().getMessageConfig().getSuccess());
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
