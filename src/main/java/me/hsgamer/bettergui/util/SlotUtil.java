package me.hsgamer.bettergui.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
  private static final Pattern RANGE_PATTERN = Pattern.compile("([0-9]+)-([0-9]+)");

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
      slots.addAll(Arrays
        .stream(String.valueOf(map.get(POS_SLOT)).split(","))
        .map(String::trim)
        .flatMap(SlotUtil::generateSlots).collect(Collectors.toList()));
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
    Matcher matcher = RANGE_PATTERN.matcher(input);
    if (matcher.matches()) {
      int s1 = Integer.parseInt(matcher.group(1));
      int s2 = Integer.parseInt(matcher.group(2));
      if (s1 <= s2) {
        return IntStream.rangeClosed(s1, s2).boxed();
      } else {
        return IntStream.rangeClosed(s2, s1).boxed().sorted(Collections.reverseOrder());
      }
    }

    try {
      return Stream.of(Integer.parseInt(input));
    } catch (Exception ignored) {
      return Stream.empty();
    }
  }
}
