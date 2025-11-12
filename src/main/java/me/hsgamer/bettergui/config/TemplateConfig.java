package me.hsgamer.bettergui.config;

import io.github.projectunified.minelib.plugin.base.Loadable;
import io.github.projectunified.minelib.plugin.postenable.PostEnable;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.ConfigBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.MapUtils;

import java.io.File;
import java.util.*;

/**
 * The list of template configurations
 */
public class TemplateConfig implements Loadable, PostEnable {
  private final File templateFolder;
  private final Map<String, Map<String, Object>> templateMap = new HashMap<>();

  public TemplateConfig(File templateFolder) {
    this.templateFolder = templateFolder;
  }

  public TemplateConfig(BetterGUI plugin) {
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
  private static Object replaceVariables(Object obj, Map<String, String> variableMap) {
    return StringReplacerApplier.replace(obj, string -> {
      for (Map.Entry<String, String> entry : variableMap.entrySet()) {
        string = string.replace("{" + entry.getKey() + "}", entry.getValue());
      }
      return string;
    });
  }

  /**
   * Set up the list
   */
  public void setup() {
    setup(templateFolder);
  }

  private void setup(File file) {
    if (file.isDirectory()) {
      for (File subFile : Objects.requireNonNull(file.listFiles())) {
        setup(subFile);
      }
      return;
    }
    if (file.isFile()) {
      ConfigBuilder.INSTANCE.build(file).ifPresent(config -> {
        config.setup();
        for (Map.Entry<String[], Object> entry : config.getNormalizedValues(false).entrySet()) {
          String key = entry.getKey()[0];
          Optional<Map<String, Object>> optionalValues = MapUtils.castOptionalStringObjectMap(entry.getValue());
          if (!optionalValues.isPresent()) {
            continue;
          }
          if (BetterGUI.getInstance().get(MainConfig.class).isIncludeMenuInTemplate()) {
            key = BetterGUI.getInstance().get(MainConfig.class).getFileName(templateFolder, file) + "/" + key;
          }
          templateMap.put(key, optionalValues.get());
        }
      });
    }
  }

  /**
   * Get the values of a template with the given setting map.
   * The setting map includes the template name and the variables.
   *
   * @param settingMap the setting map
   * @param ignoreKeys the keys to ignore when getting the values
   *
   * @return the values
   */
  public Map<String, Object> getValues(Map<String, Object> settingMap, List<String> ignoreKeys) {
    Map<String, Object> finalMap = new LinkedHashMap<>();

    List<String> ignoreKeyList = new ArrayList<>(ignoreKeys);
    ignoreKeyList.replaceAll(String::toLowerCase);

    Map<String, Object> keys = new CaseInsensitiveStringMap<>(settingMap);
    Optional.ofNullable(keys.get("template"))
      .map(String::valueOf)
      .flatMap(this::get)
      .ifPresent(finalMap::putAll);
    Map<String, String> variableMap = new HashMap<>();
    // noinspection unchecked
    Optional.ofNullable(keys.get("variable"))
      .filter(Map.class::isInstance)
      .map(Map.class::cast)
      .ifPresent(map -> map.forEach((k, v) -> {
        String variable = String.valueOf(k);
        String value;
        if (v instanceof List) {
          List<String> list = new ArrayList<>();
          ((List<?>) v).forEach(o -> list.add(String.valueOf(o)));
          value = String.join("\n", list);
        } else {
          value = String.valueOf(v);
        }
        variableMap.put(variable, value);
      }));
    keys.entrySet()
      .stream()
      .filter(entry ->
        !entry.getKey().equalsIgnoreCase("variable")
          && !entry.getKey().equalsIgnoreCase("template")
          && !ignoreKeyList.contains(entry.getKey().toLowerCase())
      )
      .forEach(entry -> finalMap.put(entry.getKey(), entry.getValue()));
    if (!variableMap.isEmpty()) {
      finalMap.replaceAll((s, o) -> replaceVariables(o, variableMap));
    }

    return finalMap;
  }

  /**
   * Get the values of a template with the given setting map.
   * The setting map includes the template name and the variables.
   *
   * @param settingMap the setting map
   * @param ignoreKeys the keys to ignore when getting the values
   *
   * @return the values
   */
  public Map<String, Object> getValues(Map<String, Object> settingMap, String... ignoreKeys) {
    return getValues(settingMap, Arrays.asList(ignoreKeys));
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

  @Override
  public void postEnable() {
    setup();
  }

  @Override
  public void disable() {
    clear();
  }
}
