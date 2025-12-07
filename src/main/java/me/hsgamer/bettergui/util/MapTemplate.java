package me.hsgamer.bettergui.util;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapTemplate {
  public static final String START_VARIABLE = "{";
  public static final String END_VARIABLE = "}";

  private static Object getVariableValue(String string, Map<String, Object> variableMap) {
    int startVariableIndex = string.indexOf(START_VARIABLE);
    int endVariableIndex = string.lastIndexOf(END_VARIABLE);
    if (startVariableIndex != 0 || endVariableIndex != string.length() - END_VARIABLE.length()) {
      return null;
    }
    String variable = string.substring(startVariableIndex + START_VARIABLE.length(), endVariableIndex);
    Object variableValue = variableMap.get(variable);
    if (variableValue == null) {
      return null;
    }
    return apply(variableValue, variableMap);
  }

  public static Collection<?> apply(Collection<?> collection, Map<String, Object> variableMap) {
    Stream<Object> stream = collection.stream()
      .flatMap(obj -> {
        if (!(obj instanceof String)) {
          return Stream.of(obj);
        }
        String string = (String) obj;
        Object variableValue = getVariableValue(string, variableMap);
        if (variableValue == null) {
          return Stream.of(obj);
        }
        if (variableValue instanceof Collection) {
          return ((Collection<?>) variableValue).stream();
        }
        return Stream.of(variableValue);
      });
    if (collection instanceof List) {
      return stream.collect(Collectors.toList());
    } else if (collection instanceof Set) {
      return stream.collect(Collectors.toSet());
    } else {
      return stream.collect(Collectors.toCollection(ArrayList::new));
    }
  }

  public static Map<?, ?> apply(Map<?, ?> map, Map<String, Object> variableMap) {
    Stream<Map.Entry<?, ?>> stream = map.entrySet().stream()
      .flatMap(entry -> {
        Object key = entry.getKey();
        if (!(key instanceof String)) {
          return Stream.of(new AbstractMap.SimpleEntry<>(key, apply(entry.getValue(), variableMap)));
        }
        String string = (String) key;
        Object variableValue = getVariableValue(string, variableMap);
        if (variableValue == null) {
          return Stream.of(new AbstractMap.SimpleEntry<>(key, apply(entry.getValue(), variableMap)));
        }
        if (variableValue instanceof Map) {
          return ((Map<?, ?>) variableValue).entrySet().stream();
        }
        return Stream.empty();
      });
    if (map instanceof ConcurrentMap) {
      return stream.collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
    } else if (map instanceof LinkedHashMap) {
      return stream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    } else {
      return stream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));
    }
  }

  public static Object apply(Object obj, Map<String, Object> variableMap) {
    if (variableMap == null || variableMap.isEmpty()) {
      return obj;
    }
    if (obj instanceof Collection) {
      return apply((Collection<?>) obj, variableMap);
    }
    if (obj instanceof Map) {
      return apply((Map<?, ?>) obj, variableMap);
    }
    if (obj instanceof String) {
      String result = (String) obj;
      StringBuilder sb = new StringBuilder(result);
      for (Map.Entry<String, Object> entry : variableMap.entrySet()) {
        String placeholder = START_VARIABLE + entry.getKey() + END_VARIABLE;
        String replacement = Objects.toString(entry.getValue());
        int index;
        while ((index = sb.indexOf(placeholder)) != -1) {
          sb.replace(index, index + placeholder.length(), replacement);
        }
      }
      return sb.toString();
    }
    return obj;
  }
}
