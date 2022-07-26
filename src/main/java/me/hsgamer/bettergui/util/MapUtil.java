package me.hsgamer.bettergui.util;

import java.util.Map;

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
    for (K k : key) {
      if (map.containsKey(k)) {
        return map.get(k);
      }
    }
    return defaultValue;
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
    return getIfFoundOrDefault(map, null, key);
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
    for (K k : key) {
      if (map.containsKey(k)) {
        return true;
      }
    }
    return false;
  }
}
