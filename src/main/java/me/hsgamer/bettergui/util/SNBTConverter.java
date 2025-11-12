package me.hsgamer.bettergui.util;

import java.util.*;

public class SNBTConverter {

  /**
   * Converts a Map<String, Object> to SNBT string format
   *
   * @param map The map to convert
   *
   * @return SNBT formatted string
   *
   * @throws IllegalArgumentException if forced-value map is invalid
   */
  public static String toSNBT(Map<String, Object> map) {
    return toSNBT(map, false);
  }

  /**
   * Converts a Map<String, Object> to SNBT string format
   *
   * @param map                    The map to convert
   * @param useDataComponentFormat If true, use Minecraft data component format
   *
   * @return SNBT formatted string
   *
   * @throws IllegalArgumentException if forced-value map is invalid
   */
  public static String toSNBT(Map<String, Object> map, boolean useDataComponentFormat) {
    if (map == null) {
      return useDataComponentFormat ? "[]" : "{}";
    }

    // Check if this is a forced-value map
    if (map.containsKey("$type")) {
      if (!map.containsKey("$value")) {
        throw new IllegalArgumentException(
          "Map with '$type' entry must also have '$value' entry");
      }
      return convertForcedValue(map.get("$type"), map.get("$value"));
    }

    return convertToCompound(map, useDataComponentFormat);
  }

  /**
   * Converts a forced-value map based on specified $type
   */
  private static String convertForcedValue(Object type, Object value) {
    if (!(type instanceof String)) {
      throw new IllegalArgumentException("Type must be a string");
    }

    String typeStr = ((String) type).toLowerCase();

    switch (typeStr) {
      case "byte":
        return convertToByte(value);
      case "boolean":
        return convertToBoolean(value);
      case "short":
        return convertToShort(value);
      case "int":
      case "integer":
        return convertToInt(value);
      case "long":
        return convertToLong(value);
      case "float":
        return convertToFloat(value);
      case "double":
        return convertToDouble(value);
      case "string":
        return convertToString(value);
      case "raw":
        return convertToRaw(value);
      case "list":
        return convertToList(value);
      case "compound":
        return convertToCompound(value);
      case "byte_array":
      case "bytearray":
        return convertToByteArray(value);
      case "int_array":
      case "intarray":
        return convertToIntArray(value);
      case "long_array":
      case "longarray":
        return convertToLongArray(value);
      default:
        throw new IllegalArgumentException("Unknown type: " + typeStr);
    }
  }

  /**
   * Converts a value to SNBT format based on its Java type
   */
  private static String convertValue(Object value) {
    if (value == null) {
      return "\"\"";
    }

    // Check for forced-value map
    if (value instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> map = (Map<String, Object>) value;
      return toSNBT(map);
    }

    // Auto-detect type based on Java type
    if (value instanceof Boolean) {
      return value.toString();
    } else if (value instanceof Byte) {
      return value + "b";
    } else if (value instanceof Short) {
      return value + "s";
    } else if (value instanceof Integer) {
      return value.toString();
    } else if (value instanceof Long) {
      return value + "L";
    } else if (value instanceof Float) {
      return value + "f";
    } else if (value instanceof Double) {
      return value.toString();
    } else if (value instanceof String) {
      String str = ((String) value).trim();

      // Check for numeric suffixes
      if (str.length() > 1) {
        char lastChar = str.charAt(str.length() - 1);
        String numPart = str.substring(0, str.length() - 1);

        try {
          switch (lastChar) {
            case 'b':
            case 'B':
              Byte.parseByte(numPart);
              return str.toLowerCase().replace('B', 'b');
            case 's':
            case 'S':
              Short.parseShort(numPart);
              return str.toLowerCase().replace('S', 's');
            case 'l':
            case 'L':
              Long.parseLong(numPart);
              return str.toUpperCase().replace('l', 'L');
            case 'f':
            case 'F':
              Float.parseFloat(numPart);
              return str.toLowerCase().replace('F', 'f');
            case 'd':
            case 'D':
              Double.parseDouble(numPart);
              return numPart; // Remove suffix for double
          }
        } catch (NumberFormatException e) {
          // Not a valid number with suffix, treat as string
        }
      }

      // Check if it's a plain number without suffix
      try {
        if (str.contains(".")) {
          Double.parseDouble(str);
          return str; // It's a double
        } else {
          Integer.parseInt(str);
          return str; // It's an int
        }
      } catch (NumberFormatException e) {
        // Not a number, treat as string
      }

      return escapeString(str);
    } else if (value instanceof List) {
      return convertToList(value);
    } else if (value instanceof byte[]) {
      return convertToByteArray(value);
    } else if (value instanceof int[]) {
      return convertToIntArray(value);
    } else if (value instanceof long[]) {
      return convertToLongArray(value);
    }

    // Fallback to string representation
    return escapeString(value.toString());
  }

  private static String convertToByte(Object value) {
    if (value instanceof Number) {
      return ((Number) value).byteValue() + "b";
    }
    if (value instanceof String) {
      String str = (String) value;
      str = str.trim();
      // Remove suffix if present
      if (str.endsWith("b") || str.endsWith("B")) {
        str = str.substring(0, str.length() - 1);
      }
      return Byte.parseByte(str) + "b";
    }
    throw new IllegalArgumentException("Cannot convert " + value + " to byte");
  }

  private static String convertToBoolean(Object value) {
    if (value instanceof Boolean) {
      return value.toString();
    }
    if (value instanceof Number) {
      return ((Number) value).intValue() != 0 ? "true" : "false";
    }
    if (value instanceof String) {
      String str = ((String) value).trim().toLowerCase();
      if (str.equals("true") || str.equals("false")) {
        return str;
      }
      // Try parsing as number
      try {
        int num = Integer.parseInt(str);
        return num != 0 ? "true" : "false";
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Cannot convert " + value + " to boolean");
      }
    }
    throw new IllegalArgumentException("Cannot convert " + value + " to boolean");
  }

  private static String convertToShort(Object value) {
    if (value instanceof Number) {
      return ((Number) value).shortValue() + "s";
    }
    if (value instanceof String) {
      String str = (String) value;
      str = str.trim();
      // Remove suffix if present
      if (str.endsWith("s") || str.endsWith("S")) {
        str = str.substring(0, str.length() - 1);
      }
      return Short.parseShort(str) + "s";
    }
    throw new IllegalArgumentException("Cannot convert " + value + " to short");
  }

  private static String convertToInt(Object value) {
    if (value instanceof Number) {
      return String.valueOf(((Number) value).intValue());
    }
    if (value instanceof String) {
      String str = (String) value;
      str = str.trim();
      // Remove suffix if present
      if (str.endsWith("i") || str.endsWith("I")) {
        str = str.substring(0, str.length() - 1);
      }
      return String.valueOf(Integer.parseInt(str));
    }
    throw new IllegalArgumentException("Cannot convert " + value + " to int");
  }

  private static String convertToLong(Object value) {
    if (value instanceof Number) {
      return ((Number) value).longValue() + "L";
    }
    if (value instanceof String) {
      String str = (String) value;
      str = str.trim();
      // Remove suffix if present
      if (str.endsWith("l") || str.endsWith("L")) {
        str = str.substring(0, str.length() - 1);
      }
      return Long.parseLong(str) + "L";
    }
    throw new IllegalArgumentException("Cannot convert " + value + " to long");
  }

  private static String convertToFloat(Object value) {
    if (value instanceof Number) {
      return ((Number) value).floatValue() + "f";
    }
    if (value instanceof String) {
      String str = (String) value;
      str = str.trim();
      // Remove suffix if present
      if (str.endsWith("f") || str.endsWith("F")) {
        str = str.substring(0, str.length() - 1);
      }
      return Float.parseFloat(str) + "f";
    }
    throw new IllegalArgumentException("Cannot convert " + value + " to float");
  }

  private static String convertToDouble(Object value) {
    if (value instanceof Number) {
      return String.valueOf(((Number) value).doubleValue());
    }
    if (value instanceof String) {
      String str = (String) value;
      str = str.trim();
      // Remove suffix if present
      if (str.endsWith("d") || str.endsWith("D")) {
        str = str.substring(0, str.length() - 1);
      }
      return String.valueOf(Double.parseDouble(str));
    }
    throw new IllegalArgumentException("Cannot convert " + value + " to double");
  }

  private static String convertToString(Object value) {
    return escapeString(value.toString());
  }

  private static String convertToRaw(Object value) {
    return value.toString();
  }

  private static String convertToList(Object value) {
    if (!(value instanceof List)) {
      throw new IllegalArgumentException("Value must be a List");
    }

    @SuppressWarnings("unchecked")
    List<Object> list = (List<Object>) value;

    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < list.size(); i++) {
      if (i > 0) sb.append(",");
      sb.append(convertValue(list.get(i)));
    }
    sb.append("]");
    return sb.toString();
  }

  private static String convertToCompound(Object value) {
    return convertToCompound(value, false);
  }

  private static String convertToCompound(Object value, boolean useDataComponentFormat) {
    if (!(value instanceof Map)) {
      throw new IllegalArgumentException("Value must be a Map");
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> map = (Map<String, Object>) value;

    StringBuilder sb = new StringBuilder();
    sb.append(useDataComponentFormat ? "[" : "{");

    boolean first = true;
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      if (!first) sb.append(",");
      first = false;

      String key = entry.getKey();
      sb.append(escapeKey(key)).append(useDataComponentFormat ? "=" : ":").append(convertValue(entry.getValue()));
    }

    sb.append(useDataComponentFormat ? "]" : "}");
    return sb.toString();
  }

  private static String convertToByteArray(Object value) {
    byte[] arr;
    if (value instanceof byte[]) {
      arr = (byte[]) value;
    } else if (value instanceof List) {
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>) value;
      arr = new byte[list.size()];
      for (int i = 0; i < list.size(); i++) {
        arr[i] = ((Number) list.get(i)).byteValue();
      }
    } else {
      throw new IllegalArgumentException("Value must be byte[] or List");
    }

    StringBuilder sb = new StringBuilder("[B;");
    for (int i = 0; i < arr.length; i++) {
      if (i > 0) sb.append(",");
      sb.append(arr[i]).append("b");
    }
    sb.append("]");
    return sb.toString();
  }

  private static String convertToIntArray(Object value) {
    int[] arr;
    if (value instanceof int[]) {
      arr = (int[]) value;
    } else if (value instanceof List) {
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>) value;
      arr = new int[list.size()];
      for (int i = 0; i < list.size(); i++) {
        arr[i] = ((Number) list.get(i)).intValue();
      }
    } else {
      throw new IllegalArgumentException("Value must be int[] or List");
    }

    StringBuilder sb = new StringBuilder("[I;");
    for (int i = 0; i < arr.length; i++) {
      if (i > 0) sb.append(",");
      sb.append(arr[i]);
    }
    sb.append("]");
    return sb.toString();
  }

  private static String convertToLongArray(Object value) {
    long[] arr;
    if (value instanceof long[]) {
      arr = (long[]) value;
    } else if (value instanceof List) {
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>) value;
      arr = new long[list.size()];
      for (int i = 0; i < list.size(); i++) {
        arr[i] = ((Number) list.get(i)).longValue();
      }
    } else {
      throw new IllegalArgumentException("Value must be long[] or List");
    }

    StringBuilder sb = new StringBuilder("[L;");
    for (int i = 0; i < arr.length; i++) {
      if (i > 0) sb.append(",");
      sb.append(arr[i]).append("L");
    }
    sb.append("]");
    return sb.toString();
  }

  /**
   * Escapes a string for SNBT format
   */
  private static String escapeString(String str) {
    if (needsQuotes(str)) {
      return "\"" + str.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
    return str;
  }

  /**
   * Escapes a key for SNBT format
   */
  private static String escapeKey(String key) {
    if (needsQuotesForKey(key)) {
      return "\"" + key.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
    return key;
  }

  /**
   * Checks if a string needs quotes
   */
  private static boolean needsQuotes(String str) {
    if (str.isEmpty()) {
      return true;
    }

    char first = str.charAt(0);
    if (Character.isDigit(first) || first == '-' || first == '.' || first == '+') {
      return true;
    }

    for (char c : str.toCharArray()) {
      if (!Character.isLetterOrDigit(c) && c != '_' && c != '-' && c != '.' && c != '+') {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if a key needs quotes
   */
  private static boolean needsQuotesForKey(String key) {
    if (key.isEmpty()) {
      return true;
    }

    for (char c : key.toCharArray()) {
      if (!Character.isLetterOrDigit(c) && c != '_' && c != '-' && c != '.' && c != '+') {
        return true;
      }
    }
    return false;
  }

  // Example usage
  public static void main(String[] args) {
    // Example 1: Simple compound
    Map<String, Object> simple = new HashMap<>();
    simple.put("X", 3);
    simple.put("Y", 64);
    simple.put("Z", 129);
    System.out.println("Simple compound: " + toSNBT(simple));
    // Output: {X:3,Y:64,Z:129}

    // Example 2: Auto-detect types from Java objects
    Map<String, Object> autoTypes = new HashMap<>();
    autoTypes.put("byteVal", (byte) 127);
    autoTypes.put("shortVal", (short) 32000);
    autoTypes.put("intVal", 2147483647);
    autoTypes.put("longVal", 9223372036854775807L);
    autoTypes.put("floatVal", 3.14f);
    autoTypes.put("doubleVal", 3.14159265359);
    autoTypes.put("boolVal", true);
    autoTypes.put("stringVal", "Hello World");
    System.out.println("Auto-detect types: " + toSNBT(autoTypes));
    // Output: {byteVal:127b,shortVal:32000s,intVal:2147483647,longVal:9223372036854775807L,floatVal:3.14f,doubleVal:3.14159265359,boolVal:true,stringVal:"Hello World"}

    // Example 3: Strings with suffixes
    Map<String, Object> stringSuffixes = new HashMap<>();
    stringSuffixes.put("byte", "100b");
    stringSuffixes.put("short", "5000s");
    stringSuffixes.put("long", "999999L");
    stringSuffixes.put("float", "2.5f");
    stringSuffixes.put("double", "1.234");
    stringSuffixes.put("int", "42");
    System.out.println("String suffixes: " + toSNBT(stringSuffixes));
    // Output: {byte:100b,short:5000s,long:999999L,float:2.5f,double:1.234,int:42}

    // Example 4: Forced types with $type/$value maps
    Map<String, Object> forcedByte = new HashMap<>();
    forcedByte.put("$type", "byte");
    forcedByte.put("$value", 127);

    Map<String, Object> forcedShort = new HashMap<>();
    forcedShort.put("$type", "short");
    forcedShort.put("$value", "31415s");

    Map<String, Object> forcedLong = new HashMap<>();
    forcedLong.put("$type", "long");
    forcedLong.put("$value", "1000000");

    Map<String, Object> forcedContainer = new HashMap<>();
    forcedContainer.put("health", forcedByte);
    forcedContainer.put("score", forcedShort);
    forcedContainer.put("timestamp", forcedLong);
    System.out.println("Forced types: " + toSNBT(forcedContainer));
    // Output: {health:127b,score:31415s,timestamp:1000000L}

    // Example 5: Nested structures
    Map<String, Object> innerMap = new HashMap<>();
    innerMap.put("x", 10);
    innerMap.put("y", 20);

    Map<String, Object> nested = new HashMap<>();
    nested.put("foo", 1);
    nested.put("bar", "abc");
    nested.put("position", innerMap);
    System.out.println("Nested: " + toSNBT(nested));
    // Output: {foo:1,bar:abc,position:{x:10,y:20}}

    // Example 6: Lists
    Map<String, Object> withList = new HashMap<>();
    withList.put("numbers", Arrays.asList(1, 2, 3, 4, 5));
    withList.put("floats", Arrays.asList(1.5f, 2.5f, 3.5f));
    withList.put("mixed", Arrays.asList(1, "text", 3.14));
    System.out.println("Lists: " + toSNBT(withList));
    // Output: {numbers:[1,2,3,4,5],floats:[1.5f,2.5f,3.5f],mixed:[1,text,3.14]}

    // Example 7: Byte arrays
    Map<String, Object> byteArrayType = new HashMap<>();
    byteArrayType.put("$type", "byte_array");
    byteArrayType.put("$value", Arrays.asList(1, 2, 3));

    Map<String, Object> withByteArray = new HashMap<>();
    withByteArray.put("data", byteArrayType);
    withByteArray.put("raw", new byte[]{10, 20, 30});
    System.out.println("Byte arrays: " + toSNBT(withByteArray));
    // Output: {data:[B;1b,2b,3b],raw:[B;10b,20b,30b]}

    // Example 8: Int arrays
    Map<String, Object> intArrayType = new HashMap<>();
    intArrayType.put("$type", "int_array");
    intArrayType.put("$value", Arrays.asList(100, 200, 300));

    Map<String, Object> withIntArray = new HashMap<>();
    withIntArray.put("coords", intArrayType);
    withIntArray.put("raw", new int[]{1, 2, 3});
    System.out.println("Int arrays: " + toSNBT(withIntArray));
    // Output: {coords:[I;100,200,300],raw:[I;1,2,3]}

    // Example 9: Long arrays
    Map<String, Object> longArrayType = new HashMap<>();
    longArrayType.put("$type", "long_array");
    longArrayType.put("$value", Arrays.asList(1000L, 2000L, 3000L));

    Map<String, Object> withLongArray = new HashMap<>();
    withLongArray.put("uuids", longArrayType);
    withLongArray.put("raw", new long[]{111L, 222L, 333L});
    System.out.println("Long arrays: " + toSNBT(withLongArray));
    // Output: {uuids:[L;1000L,2000L,3000L],raw:[L;111L,222L,333L]}

    // Example 10: Raw strings
    Map<String, Object> rawType = new HashMap<>();
    rawType.put("$type", "raw");
    rawType.put("$value", "unquoted_value");

    Map<String, Object> stringType = new HashMap<>();
    stringType.put("$type", "string");
    stringType.put("$value", "quoted value");

    Map<String, Object> withRaw = new HashMap<>();
    withRaw.put("raw", rawType);
    withRaw.put("normal", stringType);
    System.out.println("Raw vs String: " + toSNBT(withRaw));
    // Output: {raw:unquoted_value,normal:"quoted value"}

    // Example 11: Special characters in strings
    Map<String, Object> specialChars = new HashMap<>();
    specialChars.put("quote", "He said \"Hello\"");
    specialChars.put("backslash", "C:\\path\\to\\file");
    specialChars.put("key with spaces", "value");
    System.out.println("Special chars: " + toSNBT(specialChars));
    // Output: {"key with spaces":value,quote:"He said \"Hello\"",backslash:"C:\\path\\to\\file"}

    // Example 12: Boolean values
    Map<String, Object> booleans = new HashMap<>();
    booleans.put("enabled", true);
    booleans.put("disabled", false);

    Map<String, Object> boolFromNum = new HashMap<>();
    boolFromNum.put("$type", "boolean");
    boolFromNum.put("$value", 1);

    booleans.put("fromNumber", boolFromNum);
    System.out.println("Booleans: " + toSNBT(booleans));
    // Output: {enabled:true,disabled:false,fromNumber:true}

    // Example 13: Complex nested example (Minecraft-like)
    Map<String, Object> item = new HashMap<>();
    item.put("id", "minecraft:diamond_sword");
    item.put("Count", (byte) 1);

    Map<String, Object> enchantment1 = new HashMap<>();
    enchantment1.put("id", "minecraft:sharpness");
    enchantment1.put("lvl", (short) 5);

    Map<String, Object> enchantment2 = new HashMap<>();
    enchantment2.put("id", "minecraft:unbreaking");
    enchantment2.put("lvl", (short) 3);

    Map<String, Object> tag = new HashMap<>();
    tag.put("Enchantments", Arrays.asList(enchantment1, enchantment2));
    tag.put("Damage", 0);

    item.put("tag", tag);
    System.out.println("Complex (Minecraft item): " + toSNBT(item));
    // Output: {id:minecraft:diamond_sword,Count:1b,tag:{Enchantments:[{id:minecraft:sharpness,lvl:5s},{id:minecraft:unbreaking,lvl:3s}],Damage:0}}

    // Example 14: Empty structures
    Map<String, Object> empty = new HashMap<>();
    empty.put("emptyMap", new HashMap<>());
    empty.put("emptyList", new ArrayList<>());
    empty.put("emptyString", "");
    System.out.println("Empty structures: " + toSNBT(empty));
    // Output: {emptyMap:{},emptyList:[],emptyString:""}

    // Example 15: Minecraft component format (without root braces)
    Map<String, Object> modifier1 = new HashMap<>();
    modifier1.put("type", "minecraft:scale");
    modifier1.put("slot", "hand");
    modifier1.put("id", "example:grow");
    modifier1.put("amount", 4);
    modifier1.put("operation", "add_multiplied_base");

    Map<String, Object> component = new HashMap<>();
    component.put("attribute_modifiers", Collections.singletonList(modifier1));

    System.out.println("Minecraft component: " + toSNBT(component, false));
    // Output: {attribute_modifiers:[{type:minecraft:scale,slot:hand,id:example:grow,amount:4,operation:add_multiplied_base}]}

    System.out.println("Minecraft component (data component format): " + toSNBT(component, true));
    // Output: [attribute_modifiers=[{type:minecraft:scale,slot:hand,id:example:grow,amount:4,operation:add_multiplied_base}]]

    // Example 16: Multiple components without braces
    Map<String, Object> multiComponent = new HashMap<>();
    multiComponent.put("custom_name", "\"Legendary Sword\"");
    multiComponent.put("damage", 100);
    multiComponent.put("enchantments", Arrays.asList("sharpness", "fire_aspect"));

    System.out.println("Multiple components (with braces): " + toSNBT(multiComponent, false));
    // Output: {custom_name:"Legendary Sword",damage:100,enchantments:[sharpness,fire_aspect]}

    System.out.println("Multiple components (data component format): " + toSNBT(multiComponent, true));
    // Output: [custom_name="Legendary Sword",damage:100,enchantments:[sharpness,fire_aspect]]
  }
}