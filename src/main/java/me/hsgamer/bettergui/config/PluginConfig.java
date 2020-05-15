package me.hsgamer.bettergui.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginConfig {

  private final File configFile;
  private final JavaPlugin plugin;
  private final String fileName;
  private FileConfiguration config;

  public PluginConfig(JavaPlugin plugin, String filename) {
    this(plugin, new File(plugin.getDataFolder(), filename));
  }

  public PluginConfig(JavaPlugin plugin, File file) {
    this.plugin = plugin;
    this.configFile = file;
    this.fileName = file.getName();
    setUpConfig();
  }

  private void setUpConfig() {
    if (!configFile.exists()) {
      try {
        configFile.createNewFile();
      } catch (IOException e) {
        plugin.getLogger().log(Level.WARNING, e, () -> "Something wrong when creating " + fileName);
      }
    }
    config = YamlConfiguration.loadConfiguration(configFile);
  }

  public void reloadConfig() {
    config = YamlConfiguration.loadConfiguration(configFile);
  }

  public void saveConfig() {
    try {
      config.save(configFile);
    } catch (IOException e) {
      plugin.getLogger().log(Level.WARNING, e, () -> "Something wrong when saving " + fileName);
    }
  }

  public FileConfiguration getConfig() {
    if (config == null) {
      setUpConfig();
    }
    return config;
  }


  @SuppressWarnings("unchecked")
  public <T> T get(Class<T> classType, String path, T def) {
    Object o = getConfig().get(path, def);
    return classType.isInstance(o) ? (T) o : def;
  }

  public <T> T get(Class<T> classType, String path) {
    return get(classType, path, null);
  }

  public String getFileName() {
    return fileName;
  }
}