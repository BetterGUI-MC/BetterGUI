package me.hsgamer.bettergui.menu;

import io.github.projectunified.craftitem.core.ItemModifier;
import io.github.projectunified.craftitem.spigot.core.SpigotItem;
import io.github.projectunified.craftux.common.*;
import io.github.projectunified.craftux.spigot.SpigotInventoryUtil;
import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.downloader.AdditionalInfoKeys;
import me.hsgamer.bettergui.downloader.AddonDownloader;
import me.hsgamer.bettergui.manager.AddonManager;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.downloader.core.object.DownloadInfo;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.logging.Level;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class AddonMenu extends BaseInventoryMenu<Mask> {
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
  private final List<ItemModifier> itemModifiers;

  public AddonMenu(Config config) {
    super(config);
    upToDateMessage = Objects.toString(config.get("&cIt's already up-to-date", new String[]{MESSAGE_PATH, "up-to-date"}));
    downloadingMessage = Objects.toString(config.get("&eDownloading {addon}", new String[]{MESSAGE_PATH, "downloading"}));
    stillDownloadingMessage = Objects.toString(config.get("&cIt's still downloading", new String[]{MESSAGE_PATH, "still-downloading"}));
    downloadFinishedMessage = Objects.toString(config.get("&aDownload finished", new String[]{MESSAGE_PATH, "download-finished"}));
    upToDateStatus = Objects.toString(config.get("&aUp-to-date", new String[]{STATUS_PATH, "up-to-date"}));
    availableStatus = Objects.toString(config.get("&aAvailable", new String[]{STATUS_PATH, "available"}));
    outdatedStatus = Objects.toString(config.get("&cOutdated", new String[]{STATUS_PATH, "outdated"}));
    Map<String, Object> itemMap = MapUtils.castOptionalStringObjectMap(configSettings.get(BUTTON_PATH)).orElseThrow(() -> new IllegalStateException("The button map must be a map"));
    itemModifiers = ItemModifierBuilder.INSTANCE.build(itemMap);
  }

  @Override
  protected Mask createMask(Map<String, Object> sectionMap) {
    return new AddonMask();
  }

  private class AddonMask implements Mask, Element {
    private final Map<DownloadInfo, AddonButton> addonButtonMap = new HashMap<>();

    @Override
    public @NotNull Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
      Map<Position, Consumer<ActionItem>> buttonMap = new HashMap<>();
      Collection<DownloadInfo> downloadInfos = getInstance().get(AddonDownloader.class).getDownloader().getLoadedDownloadInfo().values();
      int slot = 0;
      for (DownloadInfo info : downloadInfos) {
        AddonButton button = addonButtonMap.computeIfAbsent(info, downloadInfo -> {
          AddonButton addonButton = new AddonButton(downloadInfo);
          addonButton.init();
          return addonButton;
        });
        buttonMap.put(SpigotInventoryUtil.toPosition(slot++, getInventoryType()), button.apply(uuid));
      }
      return buttonMap;
    }

    @Override
    public void stop() {
      addonButtonMap.values().forEach(Element::stop);
      addonButtonMap.clear();
    }
  }

  private class AddonButton implements Button, Element {
    private final DownloadInfo downloadInfo;
    private final UnaryOperator<String> translator;
    private String status = "";

    AddonButton(DownloadInfo downloadInfo) {
      this.downloadInfo = downloadInfo;
      this.translator = s -> {
        s = s
          .replace("{status}", status)
          .replace("{name}", downloadInfo.getName())
          .replace("{version}", downloadInfo.getVersion())
          .replace("{description}", AdditionalInfoKeys.DESCRIPTION.get(downloadInfo))
          .replace("{author}", AdditionalInfoKeys.AUTHORS.get(downloadInfo).toString());
        return StringReplacerApplier.COLORIZE.replace(s);
      };
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
    public boolean apply(@NotNull UUID uuid, @NotNull ActionItem actionItem) {
      updateStatus();
      SpigotItem item = new SpigotItem();
      for (ItemModifier itemModifier : itemModifiers) {
        itemModifier.modify(item, translator);
      }
      actionItem.setItem(item.getItemStack());
      actionItem.setAction(InventoryClickEvent.class, event -> {
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
      return true;
    }
  }
}
