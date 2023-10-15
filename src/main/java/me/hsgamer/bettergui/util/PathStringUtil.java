package me.hsgamer.bettergui.util;

import me.hsgamer.hscore.config.PathString;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class PathStringUtil {
  private static final String SEPARATOR = ".";

  public static String asString(PathString pathString) {
    return PathString.toPath(SEPARATOR, pathString);
  }

  public static Map<String, Object> asStringMap(Map<PathString, Object> map) {
    return PathString.toPathMap(SEPARATOR, map);
  }

  public static Map<CaseInsensitivePathString, Object> asCaseInsensitiveMap(Map<PathString, Object> map) {
    return map.entrySet().stream()
      .collect(
        Collectors.toMap(
          entry -> new CaseInsensitivePathString(entry.getKey()),
          Map.Entry::getValue,
          (a, b) -> b,
          LinkedHashMap::new
        )
      );
  }
}
