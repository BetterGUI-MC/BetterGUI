package me.hsgamer.bettergui.manager;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Level;
import me.hsgamer.bettergui.object.addon.Addon;
import me.hsgamer.bettergui.object.addon.AddonClassLoader;
import me.hsgamer.bettergui.util.TestCase;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

public class AddonManager {

  private final Map<String, Addon> addons = new HashMap<>();
  private final File addonsDir;
  private final JavaPlugin plugin;

  public AddonManager(JavaPlugin plugin) {
    this.plugin = plugin;
    addonsDir = new File(plugin.getDataFolder(), "addon");
    if (!addonsDir.exists()) {
      addonsDir.mkdirs();
    }
  }

  public void loadAddons() {
    Map<String, Addon> addonMap = new HashMap<>();

    // Load the addon files
    for (File file : Objects.requireNonNull(addonsDir.listFiles())) {
      if (file.isFile() && file.getName().endsWith(".jar")) {
        try (AddonClassLoader loader = new AddonClassLoader(file, getClass().getClassLoader())) {
          // Get addon
          Addon addon = loader.getAddon();
          String name = addon.getDescription().getName();

          // Check duplication
          if (addonMap.containsKey(name)) {
            plugin.getLogger().log(Level.WARNING, "Addon {} duplicated", name);
            continue;
          }

          // Add to the list
          addonMap.put(name, addon);
        } catch (InvalidConfigurationException e) {
          plugin.getLogger().log(Level.WARNING, e.getMessage(), e);
        } catch (Exception e) {
          plugin.getLogger().log(Level.WARNING, "Error when loading jar", e);
        }
      }
    }

    // Sort and load the addons
    addonMap = sortAddons(addonMap);
    Map<String, Addon> finalAddons = new HashMap<>();
    for (Map.Entry<String, Addon> entry : addonMap.entrySet()) {
      Addon addon = entry.getValue();
      if (addon.onLoad()) {
        plugin.getLogger()
            .info("Loaded " + entry.getKey() + " " + addon.getDescription().getVersion());
        finalAddons.put(entry.getKey(), addon);
      }
    }

    // Store the final addons map
    addons.putAll(finalAddons);
  }

  public void enableAddon(String name) {
    Addon addon = addons.get(name);
    addon.onEnable();
    plugin.getLogger().log(Level.INFO, "Enabled {0}",
        String.join(" ", name, addon.getDescription().getVersion()));
  }

  public void disableAddon(String name) {
    Addon addon = addons.get(name);
    addon.onDisable();
    plugin.getLogger().log(Level.INFO, "Disabled {0}",
        String.join(" ", name, addon.getDescription().getVersion()));
  }

  public void enableAddons() {
    addons.keySet().forEach(this::enableAddon);
  }

  public void disableAddons() {
    addons.keySet().forEach(this::disableAddon);
  }

  public void reloadAddons() {
    disableAddons();
    addons.clear();
    loadAddons();
    enableAddons();
  }

  public Addon getAddon(String name) {
    return addons.get(name);
  }

  public boolean isAddonLoaded(String name) {
    return addons.containsKey(name);
  }

  public Collection<String> getLoadedAddons() {
    return addons.keySet();
  }

  private Map<String, Addon> sortAddons(Map<String, Addon> original) {
    Map<String, Addon> sorted = new LinkedHashMap<>();
    Map<String, Addon> remaining = new HashMap<>();

    // Start with addons with no dependency and get the remaining
    TestCase<Entry<String, Addon>> testCase = new TestCase<Map.Entry<String, Addon>>()
        .setPredicate(
            entry -> entry.getValue().getDescription().getDepends().isEmpty() && entry.getValue()
                .getDescription().getSoftDepends()
                .isEmpty())
        .setSuccessConsumer(entry -> sorted.put(entry.getKey(), entry.getValue()))
        .setFailConsumer(entry -> remaining.put(entry.getKey(), entry.getValue()));
    original.entrySet().forEach(entry -> testCase.setTestObject(entry).test());

    // Organize the remaining
    while (!remaining.isEmpty()) {
      Map<String, Addon> tempMap = new HashMap<>();

      remaining.forEach((name, addon) -> {
        List<String> depends = addon.getDescription().getDepends();
        List<String> softDepends = addon.getDescription().getSoftDepends();

        // Filter
        depends.removeIf(sorted::containsKey);
        softDepends.removeIf(softDepend ->
            sorted.containsKey(softDepend) || !original.containsKey(softDepend));

        // Check if the required dependencies are loaded
        for (String depend : depends) {
          if (!original.containsKey(depend)) {
            plugin.getLogger().warning("Missing dependency for " + name + ": " + depend);
            return;
          }
        }

        if (depends.isEmpty() && softDepends.isEmpty()) {
          sorted.put(name, addon);
        } else {
          tempMap.put(name, addon);
        }
      });

      remaining.clear();
      remaining.putAll(tempMap);
    }

    return sorted;
  }
}
