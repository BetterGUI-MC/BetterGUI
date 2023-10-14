package me.hsgamer.bettergui.util;

import me.hsgamer.hscore.config.PathString;

import java.util.Map;

public final class PathStringUtil {
  private static final String SEPARATOR = ".";

  public static String asString(PathString pathString) {
    return PathString.toPath(SEPARATOR, pathString);
  }

  public static Map<String, Object> asStringMap(Map<PathString, Object> map) {
    return PathString.toPathMap(SEPARATOR, map);
  }
}
