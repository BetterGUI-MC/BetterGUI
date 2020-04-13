package me.hsgamer.bettergui.manager;

import static me.hsgamer.bettergui.object.addon.AddonDescription.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.object.addon.Addon;
import me.hsgamer.bettergui.object.addon.AddonClassLoader;
import me.hsgamer.bettergui.object.addon.AddonDescription;
import me.hsgamer.bettergui.util.TestCase;
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.command.Command;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public final class AddonManager {

  private final Map<String, Addon> addons = new HashMap<>();
  private final Map<Addon, AddonClassLoader> loaderMap = new HashMap<>();
  private final Map<Addon, List<Command>> commands = new HashMap<>();
  private final Map<Addon, List<Listener>> listeners = new HashMap<>();
  private final File addonsDir;
  private final BetterGUI plugin;

  public AddonManager(BetterGUI plugin) {
    this.plugin = plugin;
    addonsDir = new File(plugin.getDataFolder(), "addon");
    if (!addonsDir.exists()) {
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
    if (data.isSet(Settings.PLUGIN_DEPEND)) {
      addonDescription.setPluginDepends(data.getStringList(Settings.PLUGIN_DEPEND));
    }

    return addonDescription;
  }

  public void loadAddons() {
    Map<String, Addon> addonMap = new HashMap<>();

    // Load the addon files
    for (File file : Objects.requireNonNull(addonsDir.listFiles())) {
      if (file.isFile() && file.getName().endsWith(".jar")) {
        try (JarFile jar = new JarFile(file)) {
          // Get addon description
          AddonDescription addonDescription = getAddonDescription(jar);
          if (addonMap.containsKey(addonDescription.getName())) {
            plugin.getLogger().warning("Duplicated addon " + addonDescription.getName());
            continue;
          }

          // Try to load the addon
          AddonClassLoader loader = new AddonClassLoader(this, file, addonDescription,
              getClass().getClassLoader());
          Addon addon = loader.getAddon();

          addonMap.put(addonDescription.getName(), loader.getAddon());
          loaderMap.put(addon, loader);
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
      try {
        if (addon.onLoad()) {
          plugin.getLogger()
              .info("Loaded " + entry.getKey() + " " + addon.getDescription().getVersion());
          finalAddons.put(entry.getKey(), addon);
        } else {
          plugin.getLogger().warning(
              "Failed to load " + entry.getKey() + " " + addon.getDescription().getVersion());
          closeClassLoader(addon);
        }
      } catch (Exception e) {
        plugin.getLogger().log(Level.WARNING, "Error when loading " + entry.getKey(), e);
        closeClassLoader(addon);
      }
    }

    // Store the final addons map
    addons.putAll(finalAddons);
  }

  public void enableAddon(String name) {
    Addon addon = addons.get(name);
    try {
      addon.onEnable();
      plugin.getLogger().log(Level.INFO, "Enabled {0}",
          String.join(" ", name, addon.getDescription().getVersion()));
    } catch (Exception e) {
      plugin.getLogger().log(Level.WARNING, "Error when enabling " + name, e);
      closeClassLoader(addons.remove(name));
    }
  }

  public void disableAddon(String name) {
    Addon addon = addons.get(name);
    try {
      addon.onDisable();
      // Unregister all listeners
      listeners.computeIfPresent(addon, (a, list) -> {
        list.forEach(HandlerList::unregisterAll);
        return null;
      });
      // Unregister all commands
      commands.computeIfPresent(addon, (a, list) -> {
        list.forEach(command -> plugin.getCommandManager().unregister(command));
        return null;
      });
      plugin.getLogger().log(Level.INFO, "Disabled {0}",
          String.join(" ", name, addon.getDescription().getVersion()));
    } catch (Exception e) {
      plugin.getLogger().log(Level.WARNING, "Error when disabling " + name, e);
      closeClassLoader(addons.remove(name));
    }
  }

  public void enableAddons() {
    addons.keySet().forEach(this::enableAddon);
  }

  public void callPostEnable() {
    addons.values().forEach(Addon::onPostEnable);
  }

  public void callReload() {
    addons.values().forEach(Addon::onReload);
  }

  public void disableAddons() {
    addons.keySet().forEach(this::disableAddon);
    addons.values().forEach(this::closeClassLoader);
    addons.clear();
  }

  private void closeClassLoader(Addon addon) {
    loaderMap.computeIfPresent(addon, (a, loader) -> {
      try {
        loader.close();
      } catch (IOException e) {
        plugin.getLogger().log(Level.WARNING, "Error when closing ClassLoader", e);
      }
      return null;
    });
  }

  public void registerListener(Addon addon, Listener listener) {
    listeners.putIfAbsent(addon, new ArrayList<>());
    plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    listeners.get(addon).add(listener);
  }

  public void unregisterListener(Addon addon, Listener listener) {
    listeners.computeIfPresent(addon, (a, list) -> {
      if (list.contains(listener)) {
        list.remove(listener);
        HandlerList.unregisterAll(listener);
      }
      return list;
    });
  }

  public void registerCommand(Addon addon, Command command) {
    commands.putIfAbsent(addon, new ArrayList<>());
    plugin.getCommandManager().register(command);
    commands.get(addon).add(command);
  }

  public void unregisterCommand(Addon addon, Command command) {
    commands.computeIfPresent(addon, (a, list) -> {
      if (list.contains(command)) {
        list.remove(command);
        plugin.getCommandManager().unregister(command);
      }
      return list;
    });
  }

  public void reloadAddons() {
    disableAddons();
    loadAddons();
    enableAddons();
    callPostEnable();
  }

  @SuppressWarnings("unused")
  public Addon getAddon(String name) {
    return addons.get(name);
  }

  @SuppressWarnings("unused")
  public boolean isAddonLoaded(String name) {
    return addons.containsKey(name);
  }

  public Map<String, Addon> getLoadedAddons() {
    return addons;
  }

  private Map<String, Addon> sortAddons(Map<String, Addon> original) {
    Map<String, Addon> sorted = new LinkedHashMap<>();
    Map<String, Addon> remaining = new HashMap<>();

    // Start with addons with no dependency and get the remaining
    TestCase<Entry<String, Addon>> testCase = new TestCase<Map.Entry<String, Addon>>()
        .setPredicate(
            entry -> entry.getValue().getDescription().getDepends().isEmpty()
                && entry.getValue().getDescription().getSoftDepends().isEmpty()
                && entry.getValue().getDescription().getPluginDepends().isEmpty())
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

        // Check if the required plugins are enabled
        List<String> missing = Validate
            .getMissingDepends(addon.getDescription().getPluginDepends());
        if (!missing.isEmpty()) {
          plugin.getLogger().warning("Missing plugin dependency for " + name + ": " + Arrays
              .toString(missing.toArray()));
          closeClassLoader(addon);
          return;
        }

        // Check if the required dependencies are loaded
        for (String depend : depends) {
          if (!original.containsKey(depend)) {
            plugin.getLogger().warning("Missing dependency for " + name + ": " + depend);
            closeClassLoader(addon);
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

  public Class<?> findClass(Addon addon, String name) {
    for (AddonClassLoader loader : loaderMap.values()) {
      if (loaderMap.containsKey(addon)) {
        continue;
      }
      Class<?> clazz = loader.findClass(name, false);
      if (clazz != null) {
        return clazz;
      }
    }
    return null;
  }

  public Map<String, Integer> getAddonCount() {
    Map<String, Integer> map = new HashMap<>();
    Set<String> list = addons.keySet();
    if (list.isEmpty()) {
      map.put("Empty", 1);
    } else {
      list.forEach(s -> map.put(s, 1));
    }
    return map;
  }
}
