package me.hsgamer.bettergui.config;

import me.hsgamer.bettergui.builder.ConfigBuilder;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

/**
 * The list of template configurations
 */
public class TemplateConfig {
  private final File templateFolder;
  private final Map<String, Map<String, Object>> templateMap = new HashMap<>();

  public TemplateConfig(File templateFolder) {
    this.templateFolder = templateFolder;
  }

  public TemplateConfig(Plugin plugin) {
    this(new File(plugin.getDataFolder(), "template"));
    if (!templateFolder.exists() && templateFolder.mkdirs()) {
      plugin.saveResource("template" + File.separator + "example-template.yml", false);
    }
  }

  /**
   * Replace the variables in the object
   *
   * @param obj         the object
   * @param variableMap the variable map
   *
   * @return the replaced object
   */
  public static Object replaceVariables(Object obj, Map<String, String> variableMap) {
    if (obj instanceof String) {
      String string = (String) obj;
      for (Map.Entry<String, String> entry : variableMap.entrySet()) {
        string = string.replace("{" + entry.getKey() + "}", entry.getValue());
      }
      return string;
    } else if (obj instanceof Collection) {
      List<Object> replaceList = new ArrayList<>();
      ((Collection<?>) obj).forEach(o -> replaceList.add(replaceVariables(o, variableMap)));
      return replaceList;
    } else if (obj instanceof Map) {
      // noinspection unchecked, rawtypes
      ((Map) obj).replaceAll((k, v) -> replaceVariables(v, variableMap));
    }
    return obj;
  }

  /**
   * Set up the list
   */
  public void setup() {
    if (!templateFolder.isDirectory()) {
      return;
    }
    for (File subFile : Objects.requireNonNull(templateFolder.listFiles())) {
      if (!subFile.isFile()) {
        return;
      }
      ConfigBuilder.INSTANCE.build(subFile).ifPresent(config -> {
        config.setup();
        for (String key : config.getKeys(false)) {
          Map<String, Object> values = config.getNormalizedValues(key, false);
          templateMap.put(key, values);
        }
      });
    }
  }

  /**
   * Clear the list
   */
  public void clear() {
    this.templateMap.clear();
  }

  /**
   * Get the config values of a template
   *
   * @param name the name of the template
   *
   * @return the values as map
   */
  public Optional<Map<String, Object>> get(String name) {
    return Optional.ofNullable(this.templateMap.get(name));
  }

  /**
   * Get the names of all registered templates
   *
   * @return the names
   */
  public Collection<String> getAllTemplateNames() {
    return this.templateMap.keySet();
  }
}
