package me.hsgamer.bettergui.manager;

import static me.hsgamer.bettergui.object.addon.AddonDescription.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import me.hsgamer.bettergui.object.addon.Addon;
import me.hsgamer.bettergui.object.addon.AddonDescription;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class AddonManager {

  private final Map<String, Addon> addons = new HashMap<>();
  private final File addonsDir;
  private final JavaPlugin plugin;

  public AddonManager(JavaPlugin plugin) {
    this.plugin = plugin;
    addonsDir = new File(plugin.getDataFolder(), "addon");
    if (!addonsDir.isDirectory()) {
      addonsDir.mkdirs();
    }
  }

  private AddonDescription getAddonDescription(JarFile jar)
      throws IOException, InvalidConfigurationException {
    // Load addon.yml file
    JarEntry entry = jar.getJarEntry("addon.yml");
    if (entry == null) {
      throw new NoSuchFileException(
          "Addon '" + jar.getName() + "' doesn't contain addon.yml file");
    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(entry)));
    YamlConfiguration data = new YamlConfiguration();
    data.load(reader);

    // Load required descriptions
    String name = data.getString(Settings.NAME);
    String version = data.getString(Settings.VERSION);
    String mainClass = data.getString(Settings.CLASSPATH);
    if (name == null) {
      throw new InvalidConfigurationException(
          "Addon '" + jar.getName() + "' doesn't have a name on addon.yml");
    }
    if (version == null) {
      throw new InvalidConfigurationException(
          "Addon '" + jar.getName() + "' doesn't have a version on addon.yml");
    }
    if (mainClass == null) {
      throw new InvalidConfigurationException(
          "Addon '" + jar.getName() + "' doesn't have a main class on addon.yml");
    }
    AddonDescription addonDescription = new AddonDescription(name, version, mainClass);

    // Set optional descriptions
    if (data.isSet(Settings.AUTHORS)) {
      addonDescription.setAuthors(data.getStringList(Settings.AUTHORS));
    }
    if (data.isSet(Settings.DESCRIPTION)) {
      addonDescription.setDescription(data.getString(Settings.DESCRIPTION));
    }
    if (data.isSet(Settings.DEPEND)) {
      addonDescription.setDepends(data.getStringList(Settings.DEPEND));
    }
    if (data.isSet(Settings.SOFT_DEPEND)) {
      addonDescription.setSoftDepends(data.getStringList(Settings.SOFT_DEPEND));
    }

    return addonDescription;
  }

  public void loadAddons() {
    Map<String, Addon> addonMap = new HashMap<>();

    // Load the addon files
    for (File file : addonsDir.listFiles()) {
      try (JarFile jar = new JarFile(file)) {
        ClassLoader loader = URLClassLoader.newInstance(
            new URL[]{file.toURI().toURL()},
            getClass().getClassLoader()
        );

        // Get addon description
        AddonDescription addonDescription = getAddonDescription(jar);
        if (addonMap.containsKey(addonDescription.getName())) {
          plugin.getLogger().warning("Duplicated addon " + addonDescription.getName());
          continue;
        }

        // Try to load the addon
        Class<?> clazz = Class.forName(addonDescription.getMainClass(), true, loader);
        Class<? extends Addon> newClass = clazz.asSubclass(Addon.class);
        Constructor<? extends Addon> constructor = newClass.getConstructor();
        Addon addon = constructor.newInstance();
        addon.setDescription(addonDescription);
        addonMap.put(addonDescription.getName(), addon);
      } catch (InvalidConfigurationException e) {
        plugin.getLogger().log(Level.WARNING, e.getMessage(), e);
      } catch (Exception e) {
        plugin.getLogger().log(Level.WARNING, "Error when loading jar", e);
      }
    }

    // Sort and load the addons
    addonMap = sortAddons(addonMap);
    Map<String, Addon> finalAddons = new HashMap<>();
    for (Map.Entry<String, Addon> entry : addonMap.entrySet()) {
      Addon addon = entry.getValue();
      if (addon.onLoad()) {
        plugin.getLogger().info("Loaded " + entry.getKey());
        finalAddons.put(entry.getKey(), addon);
      }
    }

    // Store the final addons map
    addons.putAll(finalAddons);
  }

  public void enableAddon(String name) {
    addons.get(name).onEnable();
    plugin.getLogger().log(Level.INFO, "Enabled {0}", name);
  }

  public void disableAddon(String name) {
    addons.get(name).onDisable();
    plugin.getLogger().log(Level.INFO, "Disabled {0}", name);
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

  private Map<String, Addon> sortAddons(Map<String, Addon> original) {
    Map<String, Addon> sorted = new LinkedHashMap<>();
    Map<String, Addon> remaining = new HashMap<>();

    // Start with addons with no dependency and get the remaining
    original.forEach((name, addon) -> {
      if (addon.getDescription().getDepends().isEmpty() && addon.getDescription().getSoftDepends()
          .isEmpty()) {
        sorted.put(name, addon);
      } else {
        remaining.put(name, addon);
      }
    });

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
