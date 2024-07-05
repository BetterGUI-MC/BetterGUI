package me.hsgamer.bettergui.util;

import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.minecraft.gui.GUIProperties;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

/**
 * The utility class for ticks and milliseconds
 */
public class TickUtil {
  /**
   * Convert ticks to milliseconds
   *
   * @param ticks the ticks
   *
   * @return the milliseconds
   */
  public static long ticksToMillis(long ticks) {
    return ticks * GUIProperties.getMillisPerTick();
  }

  /**
   * Convert FPS (Frame-per-second) to milliseconds
   *
   * @param fps the FPS
   *
   * @return the milliseconds
   */
  public static long fpsToMillis(double fps) {
    return (long) (1000L / fps);
  }

  /**
   * Get the milliseconds from the input.
   * The input can be:
   * - A number (in ticks)
   * - A number with "t" at the end (in ticks)
   * - A number with "ms" at the end (in milliseconds)
   * - A number with "fps" at the end (in FPS)
   *
   * @param input the input
   *
   * @return the milliseconds
   */
  public static Optional<Long> toMillis(String input) {
    String lowerCase = input.toLowerCase(Locale.ROOT);

    String number;
    Function<Number, Long> function;
    if (lowerCase.endsWith("t")) {
      number = input.substring(0, input.length() - 1);
      function = n -> ticksToMillis(n.longValue());
    } else if (lowerCase.endsWith("ms")) {
      number = input.substring(0, input.length() - 2);
      function = Number::longValue;
    } else if (lowerCase.endsWith("fps")) {
      number = input.substring(0, input.length() - 3);
      function = n -> fpsToMillis(n.doubleValue());
    } else {
      number = input;
      function = n -> ticksToMillis(n.longValue());
    }

    return Validate.getNumber(number).map(function);
  }
}
