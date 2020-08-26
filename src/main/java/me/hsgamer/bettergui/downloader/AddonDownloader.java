package me.hsgamer.bettergui.downloader;

import fr.mrmicky.fastinv.FastInv;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.downloader.AddonInfo.Info;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.web.WebUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")
public class AddonDownloader {

  private static final String ADDONS_DB = "https://raw.githubusercontent.com/BetterGUI-MC/Addon-List/master/addons.json";
  private final List<AddonInfo> addonInfoList = new ArrayList<>();
  private final BetterGUI instance;
  private AddonMenu addonMenu;

  public AddonDownloader(BetterGUI instance) {
    addAddonInfos();
    this.instance = instance;
  }

  public void createMenu() {
    addonMenu = new AddonMenu();
  }

  public void cancelTask() {
    addonMenu.cancelTask();
  }

  public void openMenu(Player player) {
    addonMenu.open(player);
  }

  private void addAddonInfos() {
    CompletableFuture.supplyAsync(() -> {
      try {
        return WebUtils.getJSONFromURL(ADDONS_DB);
      } catch (IOException | ParseException e) {
        instance.getLogger()
            .log(Level.WARNING, e, () -> "Something wrong when getting the addon info");
        return null;
      }
    }).thenAccept(jsonObject -> {
      if (!(jsonObject instanceof JSONObject)) {
        return;
      }

      ((JSONObject) jsonObject).forEach((key, raw) -> {
        JSONObject value = (JSONObject) raw;

        String name = String.valueOf(key);
        String version = String.valueOf(value.get(Info.VERSION));
        String directLink = String.valueOf(value.get(Info.DIRECT_LINK));

        AddonInfo addonInfo = new AddonInfo(name, version, directLink);

        if (value.containsKey(Info.AUTHORS)) {
          JSONArray jsonArray = (JSONArray) value.get(Info.AUTHORS);
          jsonArray.forEach(o -> addonInfo.addAuthor(String.valueOf(o)));
        }

        if (value.containsKey(Info.DESCRIPTION)) {
          addonInfo.setDescription(String.valueOf(value.get(Info.DESCRIPTION)));
        }

        if (value.containsKey(Info.SOURCE_LINK)) {
          addonInfo.setSourceLink(String.valueOf(value.get(Info.SOURCE_LINK)));
        }

        if (value.containsKey(Info.WIKI)) {
          addonInfo.setWiki(String.valueOf(value.get(Info.SOURCE_LINK)));
        }

        addonInfoList.add(addonInfo);
      });
    });
  }

  private class AddonMenu extends FastInv {

    private final BukkitTask updateTask;

    public AddonMenu() {
      super(54, MessageUtils.colorize("&4&lAddon Downloader"));
      generateItems();
      updateTask = new BukkitRunnable() {
        @Override
        public void run() {
          generateItems();
        }
      }.runTaskTimer(instance, 0, 2000);
    }

    private void generateItems() {
      int slot = 0;
      for (AddonInfo addonInfo : addonInfoList) {
        ClickableItem item = addonInfo.getIcon();
        setItem(slot, item.getItem(), item.getClickEvent());
        slot++;
      }
      clearItem(slot);
    }

    private void clearItem(int startSlot) {
      for (int i = startSlot; i < getInventory().getSize(); i++) {
        removeItem(i);
      }
    }

    private void cancelTask() {
      updateTask.cancel();
    }
  }
}
