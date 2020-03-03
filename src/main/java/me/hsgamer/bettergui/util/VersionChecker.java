package me.hsgamer.bettergui.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class VersionChecker {

  private final Plugin plugin;
  private final int resourceId;

  public VersionChecker(Plugin plugin, int resourceId) {
    this.plugin = plugin;
    this.resourceId = resourceId;
  }

  public void getVersion(final Consumer<String> consumer) {
    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try (InputStream inputStream = new URL(
          "https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId)
          .openStream(); Scanner scanner = new Scanner(inputStream)) {
        if (scanner.hasNext()) {
          consumer.accept(scanner.next());
        }
      } catch (IOException exception) {
        this.plugin.getLogger().warning("Cannot look for updates: " + exception.getMessage());
      }
    });
  }
}