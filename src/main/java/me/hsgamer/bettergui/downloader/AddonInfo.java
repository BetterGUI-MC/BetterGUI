package me.hsgamer.bettergui.downloader;

import com.cryptomorin.xseries.XMaterial;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.util.CommonUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AddonInfo {

  private final String name;
  private final String version;
  private final List<String> authors = new ArrayList<>();
  private final String directLink;
  private String description = "";
  private String sourceLink = "";

  private boolean isDownloading = false;

  AddonInfo(String name, String version, String directLink) {
    this.name = name;
    this.version = version;
    this.directLink = directLink;
  }

  void addAuthor(String author) {
    this.authors.add(author);
  }

  void setSourceLink(String sourceLink) {
    this.sourceLink = sourceLink;
  }

  void setDescription(String description) {
    this.description = description;
  }

  void download() throws IOException {
    if (isDownloading) {
      throw new DownloadingException();
    }

    isDownloading = true;
    try {
      FileUtils.copyURLToFile(new URL(directLink),
          new File(BetterGUI.getInstance().getAddonManager().getAddonsDir(), name + "-" + ".jar"),
          3000, 3000);
      isDownloading = false;
    } catch (IOException e) {
      isDownloading = false;
      throw e;
    }
  }

  ClickableItem getIcon() {
    XMaterial xMaterial;
    String displayName = "&f" + name + " &c- &4" + version;
    List<String> lores = new ArrayList<>();
    lores.add("&f" + description);
    lores.add("&fAuthors: &e" + Arrays.toString(authors.toArray()));
    lores.add("");
    if (BetterGUI.getInstance().getAddonManager().isAddonLoaded(name)) {
      xMaterial = XMaterial.ORANGE_WOOL;
      lores.add("&6Status: &aLOADED");
    } else {
      xMaterial = XMaterial.WHITE_WOOL;
      lores.add("&6Status: &a");
    }
    lores.add("");
    lores.add("&bLeft click &fto download");
    if (!sourceLink.isEmpty()) {
      lores.add("&bRight click &fto get the source code");
    }
    lores.replaceAll(CommonUtils::colorize);

    ItemStack itemStack = xMaterial.parseItem();
    if (itemStack == null) {
      itemStack = new ItemStack(Material.STONE);
    }
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(CommonUtils.colorize(displayName));
    itemMeta.setLore(lores);
    itemStack.setItemMeta(itemMeta);

    Consumer<InventoryClickEvent> consumer = inventoryClickEvent -> {
      HumanEntity humanEntity = inventoryClickEvent.getWhoClicked();
      ClickType clickType = inventoryClickEvent.getClick();
      if (clickType.isLeftClick()) {
        CommonUtils.sendMessage(humanEntity, "&eDownloading " + name);
        try {
          download();
        } catch (DownloadingException e) {
          CommonUtils.sendMessage(humanEntity, "&cIt's still downloading");
        } catch (IOException e) {
          BetterGUI.getInstance().getLogger()
              .log(Level.WARNING, e, () -> "Unexpected issue when downloading " + name);
          CommonUtils.sendMessage(humanEntity,
              "&cAn unexpected issue occurs when downloading. Check the console");
        }
      } else if (clickType.isRightClick() && !sourceLink.isEmpty()) {
        CommonUtils.sendMessage(humanEntity, "&bLink: &f" + sourceLink);
      }
    };

    return new ClickableItem(itemStack, consumer);
  }

  static class Info {

    static final String VERSION = "version";
    static final String DESCRIPTION = "description";
    static final String AUTHORS = "authors";
    static final String SOURCE_LINK = "source-code";
    static final String DIRECT_LINK = "direct-link";

    private Info() {

    }
  }

}
