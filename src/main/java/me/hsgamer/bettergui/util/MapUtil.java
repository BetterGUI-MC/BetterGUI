package me.hsgamer.bettergui.util;

import me.hsgamer.hscore.common.MapUtils;

import java.util.Map;
import java.util.Optional;

/**
 * The utility class for {@link Map}
 */
public final class MapUtil {
  private MapUtil() {
    // EMPTY
  }

  /**
   * Get the value given the key from the map
   *
   * @param map          the map
   * @param defaultValue the default value
   * @param key          the key
   * @param <K>          the key type
   * @param <V>          the value type
   *
   * @return the value
   */
  @SafeVarargs
  public static <K, V> V getIfFoundOrDefault(Map<K, V> map, V defaultValue, K... key) {
    return MapUtils.getIfFoundOrDefault(map, defaultValue, key);
  }

  /**
   * Get the value given the key from the map
   *
   * @param map the map
   * @param key the key
   * @param <K> the key type
   * @param <V> the value type
   *
   * @return the value, or null if not found
   */
  @SafeVarargs
  public static <K, V> V getIfFound(Map<K, V> map, K... key) {
    return MapUtils.getIfFound(map, key);
  }

  /**
   * Check if the map contains any of the keys
   *
   * @param map the map
   * @param key the key
   * @param <K> the key type
   *
   * @return true if it does
   */
  @SafeVarargs
  public static <K> boolean containsAnyKey(Map<K, ?> map, K... key) {
    return MapUtils.containsAnyKey(map, key);
  }

  /**
   * Cast to the optional string-object map
   *
   * @param object the object
   *
   * @return the map
   */
  public static Optional<Map<String, Object>> castOptionalStringObjectMap(Object object) {
    return MapUtils.castOptionalStringObjectMap(object);
  }
}
