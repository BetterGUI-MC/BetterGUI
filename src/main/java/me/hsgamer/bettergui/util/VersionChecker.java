package me.hsgamer.bettergui.util;

import com.google.common.base.Supplier;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public final class VersionChecker {

  private final int resourceId;

  public VersionChecker(int resourceId) {
    this.resourceId = resourceId;
  }

  public CompletableFuture<String> getVersion() {
    return CompletableFuture.supplyAsync((Supplier<String>) () -> {
      try {
        URL url = new URL(
            "https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=" + resourceId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.addRequestProperty("User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonObject object = new JsonParser().parse(reader).getAsJsonObject();
        reader.close();
        connection.disconnect();

        String version = object.get("current_version").getAsString();
        if (version == null) {
          throw new IOException("Cannot get the plugin version");
        }
        return version;
      } catch (IOException exception) {
        return "Error when getting version: " + exception.getMessage();
      }
    });
  }
}