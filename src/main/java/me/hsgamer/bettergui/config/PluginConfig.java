package me.hsgamer.bettergui.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginConfig {

  private FileConfiguration config;
  private final File configFile;
  private final JavaPlugin plugin;
  private final String fileName;

  public PluginConfig(JavaPlugin plugin, String filename) {
    this(plugin, new File(plugin.getDataFolder(), filename));
  }

  public PluginConfig(JavaPlugin plugin, File file) {
    this.plugin = plugin;
    this.configFile = file;
    this.fileName = file.getName();
    setUpConfig();
    getConfig().options().copyHeader(true);
    saveConfig();
  }

  private void setUpConfig() {
    if (!configFile.exists()) {
      configFile.getParentFile().mkdirs();
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
      plugin.getLogger().log(Level.WARNING, "Something wrong when saving " + fileName, e);
    }
  }

  public FileConfiguration getConfig() {
    if (config == null) {
      setUpConfig();
    }
    return config;
  }

  public <T> T get(Class<T> classType, String path, Object def) {
    return classType.cast(getConfig().get(path, def));
  }

  public String getFileName() {
    return fileName;
  }
}