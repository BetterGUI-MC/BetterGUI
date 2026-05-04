package me.hsgamer.bettergui.api.replacer;

import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.common.StringReplacer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * An extension of {@link StringReplacer} for cases where we want to choose specific replacers based on the prefix
 */
public interface LookupStringReplacer extends StringReplacer {
  static String normalizeRemaining(String remaining) {
    return remaining.startsWith("_") ? remaining.substring(1) : remaining;
  }

  /**
   * Get the {@link StringReplacer} suitable for the string
   *
   * @param original the string
   *
   * @return the pair of the replacer and the remaining of the string
   */
  @Nullable
  Pair<StringReplacer, String> lookup(String original);

  @Override
  @Nullable
  default String replace(@NotNull String original) {
    Pair<StringReplacer, String> pair = lookup(original);
    if (pair == null) {
      return null;
    } else {
      return pair.getKey().replace(normalizeRemaining(pair.getValue()));
    }
  }

  @Override
  @Nullable
  default String replace(@NotNull String original, @NotNull UUID uuid) {
    Pair<StringReplacer, String> pair = lookup(original);
    if (pair == null) {
      return null;
    } else {
      return pair.getKey().replace(normalizeRemaining(pair.getValue()), uuid);
    }
  }
}
