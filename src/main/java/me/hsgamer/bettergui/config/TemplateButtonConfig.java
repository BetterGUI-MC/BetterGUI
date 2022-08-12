package me.hsgamer.bettergui.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

/**
 * The list of template button configurations
 */
public class TemplateButtonConfig {
  private final File templateFolder;
  private final Map<String, Config> templateMap = new HashMap<>();

  public TemplateButtonConfig(Plugin plugin) {
    this.templateFolder = new File(plugin.getDataFolder(), "template");
    if (!templateFolder.exists() && templateFolder.mkdirs()) {
      plugin.saveResource("template" + File.separator + "example-template.yml", false);
    }
  }

  /**
   * Set up the list
   */
  public void setup() {
    if (!templateFolder.isDirectory()) {
      return;
    }
    for (File subFile : Objects.requireNonNull(templateFolder.listFiles())) {
      if (!subFile.isFile() || !subFile.getName().toLowerCase(Locale.ROOT).endsWith(".yml")) {
        return;
      }
      Config config = new BukkitConfig(subFile);
      config.setup();
      for (String key : config.getKeys(false)) {
        templateMap.put(key, config);
      }
    }
  }

  /**
   * Clear the list
   */
  public void clear() {
    this.templateMap.clear();
  }

  /**
   * Reload the list
   */
  public void reload() {
    this.clear();
    this.setup();
  }

  /**
   * Get the config values of a template button
   *
   * @param name the name of the template button
   *
   * @return the values as map
   */
  public Optional<Map<String, Object>> get(String name) {
    return Optional.ofNullable(this.templateMap.get(name)).map(config -> config.getNormalizedValues(name, false));
  }

  /**
   * Get the names of all registered template buttons
   *
   * @return the names
   */
  public Collection<String> getAllTemplateButtonNames() {
    return this.templateMap.keySet();
  }
}
