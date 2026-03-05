package me.hsgamer.bettergui.util;

import io.github.projectunified.craftux.common.Position;
import me.hsgamer.hscore.common.Validate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * The utility class for generating slots
 */
public class SlotUtil {
  private static final String POS_X = "position-x";
  private static final String POS_Y = "position-y";
  private static final String POS_SLOT = "slot";

  private SlotUtil() {
    // EMPTY
  }

  /**
   * Get the position from the settings
   *
   * @param map the settings
   *
   * @return the position
   */
  public static Optional<Position> getPosition(Map<String, Object> map) {
    if (map.containsKey(POS_X) || map.containsKey(POS_Y)) {
      Optional<Integer> x = Validate.getNumber(String.valueOf(map.get(POS_X))).map(BigDecimal::intValue);
      Optional<Integer> y = Validate.getNumber(String.valueOf(map.get(POS_Y))).map(BigDecimal::intValue);
      if (x.isPresent() && y.isPresent()) {
        return Optional.of(Position.of(x.get(), y.get()));
      }
    }
    return Optional.empty();
  }

  /**
   * Get the slots from the settings
   *
   * @param map the settings
   *
   * @return the slots
   */
  public static IntStream getSlot(Map<String, Object> map) {
    if (map.containsKey(POS_SLOT)) {
      return generateSlots(String.valueOf(map.get(POS_SLOT)));
    }
    return IntStream.empty();
  }

  /**
   * Create a new stream of slots
   *
   * @param input the input string
   *
   * @return the stream of slots
   */
  public static IntStream generateSlots(String input) {
    return Arrays.stream(input.split(","))
      .map(String::trim)
      .flatMapToInt(rawSlot -> {
        String[] rangeSplit = rawSlot.split("-", 2);
        if (rangeSplit.length == 2) {
          Optional<Integer> start = Validate.getNumber(rangeSplit[0]).map(BigDecimal::intValue);
          Optional<Integer> end = Validate.getNumber(rangeSplit[1]).map(BigDecimal::intValue);
          if (start.isPresent() && end.isPresent()) {
            return IntStream.rangeClosed(start.get(), end.get());
          } else {
            return IntStream.empty();
          }
        } else {
          return Validate.getNumber(rawSlot)
            .map(BigDecimal::intValue)
            .map(IntStream::of)
            .orElseGet(IntStream::empty);
        }
      });
  }
}
