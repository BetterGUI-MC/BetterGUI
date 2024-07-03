package me.hsgamer.bettergui.util;

import me.hsgamer.hscore.common.Validate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
  public static List<Integer> getSlots(Map<String, Object> map) {
    List<Integer> slots = new ArrayList<>();

    if (map.containsKey(POS_X) || map.containsKey(POS_Y)) {
      int x = 1;
      int y = 1;
      if (map.containsKey(POS_X)) {
        x = Integer.parseInt(String.valueOf(map.get(POS_X)));
      }
      if (map.containsKey(POS_Y)) {
        y = Integer.parseInt(String.valueOf(map.get(POS_Y)));
      }
      slots.add((y - 1) * 9 + x - 1);
    }
    if (map.containsKey(POS_SLOT)) {
      slots.addAll(generateSlots(String.valueOf(map.get(POS_SLOT))).collect(Collectors.toList()));
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
  public static Stream<Integer> generateSlots(String input) {
    return Arrays.stream(input.split(","))
      .map(String::trim)
      .flatMap(rawSlot -> {
        String[] rangeSplit = rawSlot.split("-", 2);
        if (rangeSplit.length == 2) {
          Optional<Integer> start = Validate.getNumber(rangeSplit[0]).map(BigDecimal::intValue);
          Optional<Integer> end = Validate.getNumber(rangeSplit[1]).map(BigDecimal::intValue);
          if (start.isPresent() && end.isPresent()) {
            return IntStream.rangeClosed(start.get(), end.get()).boxed();
          } else {
            return Stream.empty();
          }
        } else {
          return Validate.getNumber(input)
            .map(BigDecimal::intValue)
            .map(Stream::of)
            .orElseGet(Stream::empty);
        }
      });
  }
}
