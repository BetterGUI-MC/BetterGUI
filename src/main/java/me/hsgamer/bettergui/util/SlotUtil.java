package me.hsgamer.bettergui.util;

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
   * Get the slots
   *
   * @param map the value map
   *
   * @return the slots
   */
  public static IntStream getSlots(Map<String, Object> map) {
    IntStream slots = IntStream.empty();

    if (map.containsKey(POS_X) || map.containsKey(POS_Y)) {
      Optional<Integer> x = Validate.getNumber(String.valueOf(map.get(POS_X))).map(BigDecimal::intValue);
      Optional<Integer> y = Validate.getNumber(String.valueOf(map.get(POS_Y))).map(BigDecimal::intValue);
      if (x.isPresent() && y.isPresent()) {
        slots = IntStream.of((y.get() - 1) * 9 + x.get() - 1);
      }
    }

    if (map.containsKey(POS_SLOT)) {
      slots = IntStream.concat(slots, generateSlots(String.valueOf(map.get(POS_SLOT))));
    }

    return slots;
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
