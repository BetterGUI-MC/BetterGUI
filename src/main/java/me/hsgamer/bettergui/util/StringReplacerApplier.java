package me.hsgamer.bettergui.util;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.interfaces.StringReplacer;

import java.util.LinkedHashMap;

/**
 * A utility class to apply StringReplacer
 */
public final class StringReplacerApplier {
  private static final LinkedHashMap<String, StringReplacer> STRING_REPLACER_MAP = new LinkedHashMap<>();

  static {
    STRING_REPLACER_MAP.put("colorize", (original, uuid) -> MessageUtils.colorize(original));
  }

  private StringReplacerApplier() {
    // EMPTY
  }

  /**
   * Get a mutable map of string replacers
   *
   * @return the map
   */
  public static LinkedHashMap<String, StringReplacer> getStringReplacerMap() {
    return STRING_REPLACER_MAP;
  }

  /**
   * Apply the string replacers to the item builder
   *
   * @param itemBuilder the item builder
   * @param menu        the menu
   *
   * @return the item builder
   */
  public static ItemBuilder apply(ItemBuilder itemBuilder, Menu menu) {
    itemBuilder.addStringReplacer(menu.getName() + "menu-replacer", menu::replace);
    STRING_REPLACER_MAP.forEach(itemBuilder::addStringReplacer);
    return itemBuilder;
  }

  /**
   * Apply the string replacers to the item builder
   *
   * @param itemBuilder the item builder
   * @param menuElement the menu element
   *
   * @return the item builder
   */
  public static ItemBuilder apply(ItemBuilder itemBuilder, MenuElement menuElement) {
    return apply(itemBuilder, menuElement.getMenu());
  }
}
