package me.hsgamer.bettergui.util;

import io.github.projectunified.maptemplate.MapTemplate;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.element.MenuElement;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.variable.VariableManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
   * Create the map template based on the unique id
   *
   * @param uuid        the unique id
   * @param menuElement the menu element
   *
   * @return the map template
   */
  public static MapTemplate createMapTemplate(UUID uuid, MenuElement menuElement) {
    VariableManager variableManager = BetterGUI.getInstance().get(VariableManager.class);

    return MapTemplate.builder()
      .setVariableFunction(s -> {
        MenuElement currentMenuElement = menuElement;
        while (currentMenuElement != null) {
          String replaced = currentMenuElement.getStringReplacer().tryReplace(s, uuid);
          if (replaced != null) {
            return replaced;
          }
          currentMenuElement = currentMenuElement.getParent();
        }

        return variableManager.tryReplace(s, uuid);
      })
      .build();
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
    MapTemplate mapTemplate = createMapTemplate(uuid, menuElement);

    List<StringReplacer> replacers = new ArrayList<>(STRING_REPLACERS);
    replacers.add(COLORIZE);
    StringReplacer combined = StringReplacer.combine(replacers);

    return s -> {
      s = Objects.toString(mapTemplate.apply(s));
      return combined.replaceOrOriginal(s, uuid);
    };
  }

  /**
   * Get the operator to replace the string
   *
   * @param uuid the unique id
   *
   * @return the operator
   */
  public static UnaryOperator<String> getReplaceOperator(UUID uuid) {
    return getReplaceOperator(uuid, null);
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
    MapTemplate mapTemplate = createMapTemplate(uuid, menuElement);

    string = Objects.toString(mapTemplate.apply(string));

    for (StringReplacer replacer : STRING_REPLACERS) {
      String newString = replacer.tryReplace(string, uuid);
      if (newString != null) {
        string = newString;
      }
    }
    string = COLORIZE.tryReplace(string, uuid);
    return string;
  }

  /**
   * Apply the string replacers to the string
   *
   * @param string the string
   * @param uuid   the unique id
   *
   * @return the replaced string
   */
  public static String replace(String string, UUID uuid) {
    return replace(string, uuid, null);
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
}
