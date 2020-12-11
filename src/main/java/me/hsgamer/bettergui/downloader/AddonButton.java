package me.hsgamer.bettergui.downloader;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.hscore.bukkit.gui.Button;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.downloader.exception.DownloadingException;
import me.hsgamer.hscore.downloader.object.DownloadInfo;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class AddonButton implements Button {
  private final DownloadInfo downloadInfo;
  private Status status = Status.UNKNOWN;

  AddonButton(DownloadInfo downloadInfo) {
    this.downloadInfo = downloadInfo;
  }

  @Override
  public ItemStack getItemStack(UUID uuid) {
    XMaterial xMaterial;
    String displayName = "&f" + downloadInfo.getName() + " &c- &4" + downloadInfo.getVersion();
    List<String> lore = new ArrayList<>();
    lore.add("&f" + Optional.ofNullable(AdditionalInfoKeys.DESCRIPTION.get(downloadInfo)).orElse(""));
    lore.add("&fAuthors: &e" + Optional.ofNullable(AdditionalInfoKeys.AUTHORS.get(downloadInfo)).orElse(Collections.emptyList()));
    lore.add("");
    switch (status) {
      case LATEST:
        xMaterial = XMaterial.GREEN_WOOL;
        lore.add("&6Status: &aLATEST");
        break;
      case OUTDATED:
        xMaterial = XMaterial.ORANGE_WOOL;
        lore.add("&6Status: &eOUTDATED");
        break;
      case AVAILABLE:
        xMaterial = XMaterial.LIGHT_BLUE_WOOL;
        lore.add("&6Status: &bAVAILABLE");
        break;
      default:
        xMaterial = XMaterial.GRAY_WOOL;
        lore.add("&6Status: &7UNKNOWN");
        break;
    }
    lore.add("");
    lore.add("&bLeft click &fto download");
    Optional.ofNullable(AdditionalInfoKeys.SOURCE_CODE.get(downloadInfo)).ifPresent(s -> lore.add("&bRight click &fto get the source code"));
    Optional.ofNullable(AdditionalInfoKeys.WIKI.get(downloadInfo)).ifPresent(s -> lore.add("&bMiddle click &fto see the wiki"));
    lore.replaceAll(MessageUtils::colorize);

    ItemStack itemStack = xMaterial.parseItem();
    if (itemStack == null) {
      itemStack = new ItemStack(Material.STONE);
    }
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(MessageUtils.colorize(displayName));
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);

    return itemStack;
  }

  @Override
  public void handleAction(UUID uuid, InventoryClickEvent event) {
    HumanEntity humanEntity = event.getWhoClicked();
    ClickType clickType = event.getClick();
    if (clickType.isLeftClick()) {
      if (getInstance().getAddonManager().isAddonLoaded(downloadInfo.getName()) && getInstance().getAddonManager().getAddon(downloadInfo.getName()).getDescription().getVersion().equals(downloadInfo.getVersion())) {
        MessageUtils.sendMessage(humanEntity, "&cIt's already up-to-date");
        return;
      }

      MessageUtils.sendMessage(humanEntity, "&eDownloading " + downloadInfo.getName());
      CompletableFuture.supplyAsync(() -> {
        try {
          downloadInfo.download();
          return true;
        } catch (DownloadingException e) {
          MessageUtils.sendMessage(humanEntity, "&cIt's still downloading");
        } catch (IOException e) {
          getInstance().getLogger().log(Level.WARNING, e, () -> "Unexpected issue when downloading " + downloadInfo.getName());
          MessageUtils.sendMessage(humanEntity, "&cAn unexpected issue occurs when downloading. Check the console");
        }
        return false;
      }).thenAccept(complete -> {
        if (complete.equals(Boolean.TRUE)) {
          MessageUtils.sendMessage(humanEntity, MessageConfig.SUCCESS.getValue());
        }
      });
    } else if (clickType.isRightClick()) {
      Optional.ofNullable(AdditionalInfoKeys.SOURCE_CODE.get(downloadInfo)).ifPresent(s -> MessageUtils.sendMessage(humanEntity, "&bLink: &f" + s));
    } else if (clickType.equals(ClickType.MIDDLE)) {
      Optional.ofNullable(AdditionalInfoKeys.WIKI.get(downloadInfo)).ifPresent(s -> MessageUtils.sendMessage(humanEntity, "&bLink: &f" + s));
    }
  }

  @Override
  public void init() {
    if (getInstance().getAddonManager().isAddonLoaded(downloadInfo.getName())) {
      if (getInstance().getAddonManager().getAddon(downloadInfo.getName()).getDescription().getVersion().equals(downloadInfo.getVersion())) {
        status = Status.LATEST;
      } else {
        status = Status.OUTDATED;
      }
    } else {
      status = Status.AVAILABLE;
    }
  }

  @Override
  public void stop() {
    // EMPTY
  }

  private enum Status {
    AVAILABLE,
    OUTDATED,
    LATEST,
    UNKNOWN
  }
}
