package me.hsgamer.bettergui.api.menu;

import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathString;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The menu with the standard settings.
 * The standard settings are the settings in the format:
 * <pre>
 *   menu-settings:
 *     setting1: value1
 *     setting2: value2
 *     ...
 *
 *   config-setting1:
 *     ...
 *
 *   config-setting2:
 *     ...
 *
 *   ...
 * </pre>
 */
public abstract class StandardMenu extends Menu {
  /**
   * The menu settings
   */
  protected final Map<String, Object> menuSettings;
  /**
   * The config settings, which are not in the menu settings
   */
  protected final Map<String, Object> configSettings;

  /**
   * Create a new menu
   *
   * @param config the config
   */
  protected StandardMenu(Config config) {
    super(config);
    Map<String, Object> configValues = MapUtils.createLowercaseStringObjectMap(PathString.joinDefault(config.getNormalizedValues(false)));
    Object rawMenuSettings = configValues.get(MENU_SETTINGS_PATH);
    menuSettings = MapUtils.castOptionalStringObjectMap(rawMenuSettings)
      .map(MapUtils::createLowercaseStringObjectMap)
      .orElse(Collections.emptyMap());
    configSettings = configValues.entrySet().stream()
      .filter(entry -> !entry.getKey().equalsIgnoreCase(MENU_SETTINGS_PATH))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
  }
}
