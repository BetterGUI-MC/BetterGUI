package me.hsgamer.bettergui.api.element;

import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.common.StringReplacer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An extension of {@link WithLookupStringReplacer} for cases where we want to choose replacers based on {@link MenuElement}'s name
 */
public interface WithElementLookupStringReplacer<T extends MenuElement> extends WithLookupStringReplacer {
  /**
   * Get the elements
   *
   * @return the elements
   */
  List<T> getElements();

  /**
   * Get the prefix of the lookup string. This is for backward compatibility.
   *
   * @return the prefix
   */
  default String getPrefix() {
    return "";
  }

  /**
   * Get the {@link StringReplacer} suitable for the string
   *
   * @param original the string
   *
   * @return the pair of the replacer and the remaining of the string
   */
  @Nullable
  default Pair<StringReplacer, String> lookup(String original) {
    String prefix = getPrefix();
    if (!prefix.isEmpty() && original.startsWith(prefix)) {
      original = original.substring(prefix.length());
    }
    MenuElement found = null;
    for (MenuElement element : getElements()) {
      if (original.startsWith(element.getName())) {
        if (found == null || element.getName().length() > found.getName().length()) {
          found = element;
        }
      }
    }
    if (found == null) {
      return null;
    }
    return Pair.of(found, original.substring(found.getName().length()));
  }
}
