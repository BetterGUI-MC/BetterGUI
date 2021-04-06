package me.hsgamer.bettergui.utils;

import me.hsgamer.hscore.common.Validate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The utility class for generating slots
 */
public class SlotUtils {
  private static final String POS_X = "position-x";
  private static final String POS_Y = "position-y";
  private static final String POS_SLOT = "slot";

  private SlotUtils() {
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
      slots.addAll(Arrays
        .stream(String.valueOf(map.get(POS_SLOT)).split(","))
        .map(String::trim)
        .flatMap(SlotUtils::generateSlots).collect(Collectors.toList()));
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
    if (Validate.isValidInteger(input)) {
      return Stream.of(Integer.parseInt(input));
    } else {
      String[] split = input.split("-", 2);
      Optional<BigDecimal> optional1 = Validate.getNumber(split[0].trim());
      Optional<BigDecimal> optional2 = Validate.getNumber(split[1].trim());
      if (optional1.isPresent() && optional2.isPresent()) {
        int s1 = optional1.get().intValue();
        int s2 = optional2.get().intValue();
        if (s1 <= s2) {
          return IntStream.rangeClosed(s1, s2).boxed();
        } else {
          return IntStream.rangeClosed(s2, s1).boxed().sorted(Collections.reverseOrder());
        }
      }
    }
    return Stream.empty();
  }
}
