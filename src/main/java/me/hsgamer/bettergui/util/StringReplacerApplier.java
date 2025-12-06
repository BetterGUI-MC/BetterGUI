package me.hsgamer.bettergui.util;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.variable.VariableManager;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * A utility class to apply StringReplacer
 */
public final class StringReplacerApplier {
  /**
   * A replacer to colorize the string
   */
  public static final StringReplacer COLORIZE = ColorUtils::colorize;
  private static final List<StringReplacer> STRING_REPLACERS = new ArrayList<>();

  static {
    STRING_REPLACERS.add(COLORIZE);
  }

  private StringReplacerApplier() {
    // EMPTY
  }

  /**
   * Get the mutable list of string replacers
   *
   * @return the mutable list of string replacers
   */
  public static List<StringReplacer> getStringReplacers() {
    return STRING_REPLACERS;
  }

  /**
   * Get the operator to replace the string
   *
   * @param uuid                     the unique id
   * @param useGlobalVariableManager whether to use the global variable manager
   *
   * @return the operator
   */
  public static UnaryOperator<String> getReplaceOperator(UUID uuid, boolean useGlobalVariableManager) {
    List<StringReplacer> replacers = new ArrayList<>();
    if (useGlobalVariableManager) {
      replacers.add(BetterGUI.getInstance().get(VariableManager.class));
    }
    replacers.addAll(STRING_REPLACERS);
    StringReplacer combined = StringReplacer.combine(replacers);
    return s -> combined.replaceOrOriginal(s, uuid);
  }

  /**
   * Get the operator to replace the string
   *
   * @param uuid the unique id
   * @param menu the menu
   *
   * @return the operator
   */
  public static UnaryOperator<String> getReplaceOperator(UUID uuid, Menu menu) {
    UnaryOperator<String> replaceOperator = getReplaceOperator(uuid, false);
    return s -> {
      s = menu.getVariableManager().replaceOrOriginal(s, uuid);
      return replaceOperator.apply(s);
    };
  }

  /**
   * Get the operator to replace the string
   *
   * @param uuid        the unique id
   * @param menuElement the menu element
   *
   * @return the item builder
   */
  public static UnaryOperator<String> getReplaceOperator(UUID uuid, MenuElement menuElement) {
    return getReplaceOperator(uuid, menuElement.getMenu());
  }

  /**
   * Apply the string replacers to the string
   *
   * @param string                   the string
   * @param uuid                     the unique id
   * @param useGlobalVariableManager whether to use the global variable manager
   *
   * @return the replaced string
   */
  public static String replace(String string, UUID uuid, boolean useGlobalVariableManager) {
    String replaced = string;
    if (useGlobalVariableManager) {
      replaced = BetterGUI.getInstance().get(VariableManager.class).setVariables(replaced, uuid);
    }

    for (StringReplacer replacer : STRING_REPLACERS) {
      String newString = replacer.tryReplace(replaced, uuid);
      if (newString != null) {
        replaced = newString;
      }
    }
    return replaced;
  }

  /**
   * Apply the string replacers to the string
   *
   * @param string the string
   * @param uuid   the unique id
   * @param menu   the menu
   *
   * @return the replaced string
   */
  public static String replace(String string, UUID uuid, Menu menu) {
    String replaced = menu.getVariableManager().tryReplace(string, uuid);
    if (replaced == null) {
      return string;
    }
    return replace(replaced, uuid, false);
  }

  /**
   * Apply the string replacers to the string
   *
   * @param string      the string
   * @param uuid        the unique id
   * @param menuElement the menu element
   *
   * @return the replaced string
   */
  public static String replace(String string, UUID uuid, MenuElement menuElement) {
    return replace(string, uuid, menuElement.getMenu());
  }

  /**
   * Normalize the query to a variable
   *
   * @param query the query
   *
   * @return the normalized variable
   */
  public static String normalizeQuery(String query) {
    if (query.startsWith("papi_")) {
      return "%" + query.substring("papi_".length()) + "%";
    } else {
      return "{" + query + "}";
    }
  }

  /**
   * Recursively replace the string in the object
   *
   * @param obj      the object
   * @param replacer the replacer
   *
   * @return the object with replaced strings
   */
  public static Object replace(Object obj, UnaryOperator<String> replacer) {
    if (obj instanceof String) {
      String string = (String) obj;
      string = replacer.apply(string);
      return string;
    } else if (obj instanceof Collection) {
      List<Object> replaceList = new ArrayList<>();
      ((Collection<?>) obj).forEach(o -> replaceList.add(replace(o, replacer)));
      return replaceList;
    } else if (obj instanceof Map) {
      Map<Object, Object> replaceMap = new LinkedHashMap<>();
      ((Map<?, ?>) obj).forEach((k, v) -> replaceMap.put(k, replace(v, replacer)));
      return replaceMap;
    }
    return obj;
  }
}
