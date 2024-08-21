package me.hsgamer.bettergui.menu;

import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.downloader.AdditionalInfoKeys;
import me.hsgamer.bettergui.downloader.AddonDownloader;
import me.hsgamer.bettergui.manager.AddonManager;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.gui.event.BukkitClickEvent;
import me.hsgamer.hscore.bukkit.gui.object.BukkitItem;
import me.hsgamer.hscore.bukkit.item.BukkitItemBuilder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.downloader.core.object.DownloadInfo;
import me.hsgamer.hscore.minecraft.gui.button.Button;
import me.hsgamer.hscore.minecraft.gui.button.ButtonMap;
import me.hsgamer.hscore.minecraft.gui.button.DisplayButton;
import me.hsgamer.hscore.minecraft.gui.object.InventorySize;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
import me.hsgamer.hscore.ui.property.Initializable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class AddonMenu extends BaseInventoryMenu<ButtonMap> {
  private static final String MESSAGE_PATH = "message";
  private static final String STATUS_PATH = "status";
  private static final String BUTTON_PATH = "button";

  private final String upToDateMessage;
  private final String downloadingMessage;
  private final String stillDownloadingMessage;
  private final String downloadFinishedMessage;
  private final String upToDateStatus;
  private final String availableStatus;
  private final String outdatedStatus;

  public AddonMenu(Config config) {
    super(config);
    upToDateMessage = Objects.toString(config.get("&cIt's already up-to-date", new String[]{MESSAGE_PATH, "up-to-date"}));
    downloadingMessage = Objects.toString(config.get("&eDownloading {addon}", new String[]{MESSAGE_PATH, "downloading"}));
    stillDownloadingMessage = Objects.toString(config.get("&cIt's still downloading", new String[]{MESSAGE_PATH, "still-downloading"}));
    downloadFinishedMessage = Objects.toString(config.get("&aDownload finished", new String[]{MESSAGE_PATH, "download-finished"}));
    upToDateStatus = Objects.toString(config.get("&aUp-to-date", new String[]{STATUS_PATH, "up-to-date"}));
    availableStatus = Objects.toString(config.get("&aAvailable", new String[]{STATUS_PATH, "available"}));
    outdatedStatus = Objects.toString(config.get("&cOutdated", new String[]{STATUS_PATH, "outdated"}));
  }

  @Override
  protected ButtonMap createButtonMap() {
    Optional<Map<String, Object>> optionalMap = MapUtils.castOptionalStringObjectMap(configSettings.get(BUTTON_PATH));
    if (!optionalMap.isPresent()) {
      throw new IllegalStateException("The button map must be a map");
    }
    Map<String, Object> itemMap = optionalMap.get();

    return new ButtonMap() {

      private final Map<DownloadInfo, AddonButton> addonButtonMap = new HashMap<>();

      @Override
      public @NotNull Map<@NotNull Integer, @NotNull DisplayButton> getButtons(@NotNull UUID uuid, InventorySize inventorySize) {
        Map<Integer, DisplayButton> buttonMap = new HashMap<>();
        Collection<DownloadInfo> downloadInfos = getInstance().get(AddonDownloader.class).getDownloader().getLoadedDownloadInfo().values();
        int slot = 0;
        for (DownloadInfo info : downloadInfos) {
          AddonButton button = addonButtonMap.computeIfAbsent(info, downloadInfo -> {
            ItemBuilder<ItemStack> itemBuilder = new BukkitItemBuilder();
            ItemModifierBuilder.INSTANCE.build(itemMap).forEach(itemBuilder::addItemModifier);
            AddonButton addonButton = new AddonButton(downloadInfo, itemBuilder);
            addonButton.init();
            return addonButton;
          });
          buttonMap.put(slot++, button.display(uuid));
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
      status = getInstance().get(AddonManager.class).getExpansionClassLoader(downloadInfo.getName())
        .map(addon -> addon.getDescription().getVersion().equals(downloadInfo.getVersion()) ? upToDateStatus : outdatedStatus)
        .orElse(availableStatus);
    }

    @Override
    public void init() {
      updateStatus();
    }

    @Override
    public @Nullable DisplayButton display(@NotNull UUID uuid) {
      updateStatus();
      return new DisplayButton()
        .setItem(new BukkitItem(itemBuilder.build(uuid)))
        .setClickAction(clickEvent -> {
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
              MessageUtils.sendMessage(humanEntity, getInstance().get(MessageConfig.class).getSuccess());
            }
          } else if (clickType.isRightClick()) {
            MessageUtils.sendMessage(humanEntity, "&bLink: &f" + AdditionalInfoKeys.SOURCE_CODE.get(downloadInfo));
          } else if (clickType.equals(ClickType.MIDDLE)) {
            MessageUtils.sendMessage(humanEntity, "&bLink: &f" + AdditionalInfoKeys.WIKI.get(downloadInfo));
          }
        });
    }
  }
}
