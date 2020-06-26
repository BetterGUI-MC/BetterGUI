package me.hsgamer.bettergui.downloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.downloader.AddonInfo.Info;
import me.hsgamer.bettergui.util.JSONUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")
public class AddonManager {
  private static final String ADDONS_DB = "https://raw.githubusercontent.com/BetterGUI-MC/Addon-List/master/addons.json";
  private List<AddonInfo> addonInfoList = new ArrayList<>();

  private void addAddonInfos() {
    CompletableFuture.supplyAsync(() -> {
      try {
        return JSONUtils.getJSONFromURL(ADDONS_DB);
      } catch (IOException | ParseException e) {
        BetterGUI.getInstance().getLogger().log(Level.WARNING, e, () -> "Something wrong when getting the addon info");
        return null;
      }
    }).thenAccept(jsonObject -> {
      if (jsonObject == null) {
        return;
      }

      for (Object key : jsonObject.keySet()) {
        JSONObject value = (JSONObject) jsonObject.get(key);

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
      }
    });
  }
}
