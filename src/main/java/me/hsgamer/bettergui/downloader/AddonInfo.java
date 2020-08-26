package me.hsgamer.bettergui.downloader;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import com.cryptomorin.xseries.XMaterial;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.hscore.web.WebUtils;
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

  public AddonInfo(String name, String version, String directLink) {
    this.name = name;
    this.version = version;
    this.directLink = directLink;
  }

  public void addAuthor(String author) {
    this.authors.add(author);
  }

  public void setSourceLink(String sourceLink) {
    this.sourceLink = sourceLink;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void download() throws IOException {
    if (isDownloading) {
      throw new DownloadingException();
    }

    isDownloading = true;
    try (ReadableByteChannel readableByteChannel = Channels
        .newChannel(WebUtils.openConnection(directLink).getInputStream());
        FileOutputStream fileOutputStream = new FileOutputStream(
            new File(getInstance().getAddonManager().getAddonsDir(), name + ".jar"))) {
      fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
      isDownloading = false;
    } catch (IOException e) {
      isDownloading = false;
      throw e;
    }
  }

  public ClickableItem getIcon() {
    XMaterial xMaterial;
    String displayName = "&f" + name + " &c- &4" + version;
    List<String> lores = new ArrayList<>();
    lores.add("&f" + description);
    lores.add("&fAuthors: &e" + Arrays.toString(authors.toArray()));
    lores.add("");
    if (getInstance().getAddonManager().isAddonLoaded(name)) {
      if (getInstance().getAddonManager().getAddon(name).getDescription().getVersion()
          .equals(version)) {
        xMaterial = XMaterial.GREEN_WOOL;
        lores.add("&6Status: &aUP-TO-DATE");
      } else {
        xMaterial = XMaterial.ORANGE_WOOL;
        lores.add("&6Status: &eOUTDATED");
      }
    } else {
      xMaterial = XMaterial.LIGHT_BLUE_WOOL;
      lores.add("&6Status: &bAVAILABLE");
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
        if (getInstance().getAddonManager().isAddonLoaded(name)
            && getInstance().getAddonManager().getAddon(name)
            .getDescription().getVersion().equals(version)) {
          CommonUtils.sendMessage(humanEntity, "&cIt's already up-to-date");
          return;
        }

        CommonUtils.sendMessage(humanEntity, "&eDownloading " + name);
        CompletableFuture.supplyAsync(() -> {
          try {
            download();
            return true;
          } catch (DownloadingException e) {
            CommonUtils.sendMessage(humanEntity, "&cIt's still downloading");
          } catch (IOException e) {
            getInstance().getLogger()
                .log(Level.WARNING, e, () -> "Unexpected issue when downloading " + name);
            CommonUtils.sendMessage(humanEntity,
                "&cAn unexpected issue occurs when downloading. Check the console");
          }
          return false;
        }).thenAccept(complete -> {
          if (complete.equals(Boolean.TRUE)) {
            CommonUtils.sendMessage(humanEntity, MessageConfig.SUCCESS.getValue());
          }
        });
      } else if (clickType.isRightClick() && !sourceLink.isEmpty()) {
        CommonUtils.sendMessage(humanEntity, "&bLink: &f" + sourceLink);
      }
    };

    return new ClickableItem(itemStack, consumer);
  }

  public static class Info {

    public static final String VERSION = "version";
    public static final String DESCRIPTION = "description";
    public static final String AUTHORS = "authors";
    public static final String SOURCE_LINK = "source-code";
    public static final String DIRECT_LINK = "direct-link";

    private Info() {

    }
  }

}
