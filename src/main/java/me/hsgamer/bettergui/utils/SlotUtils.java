package me.hsgamer.bettergui.utils;

import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.hscore.common.Validate;
import org.simpleyaml.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The utility class for generating slots
 */
public class SlotUtils {
  private SlotUtils() {
    // EMPTY
  }

  /**
   * Get the slots
   *
   * @param section the section
   *
   * @return the slots
   */
  public static List<Integer> getSlots(ConfigurationSection section) {
    List<Integer> slots = new ArrayList<>();
    Map<String, Object> map = new CaseInsensitiveStringHashMap<>(section.getValues(false));

    if (map.containsKey(Setting.X) || map.containsKey(Setting.Y)) {
      int x = 1;
      int y = 1;
      if (map.containsKey(Setting.X)) {
        x = Integer.parseInt(String.valueOf(map.get(Setting.X)));
      }
      if (map.containsKey(Setting.Y)) {
        y = Integer.parseInt(String.valueOf(map.get(Setting.Y)));
      }
      slots.add((y - 1) * 9 + x - 1);
    }
    if (map.containsKey(Setting.SLOT)) {
      slots.addAll(Arrays
        .stream(String.valueOf(map.get(Setting.SLOT)).split(","))
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

  public static class Setting {

    static final String X = "position-x";
    static final String Y = "position-y";
    static final String SLOT = "slot";

    private Setting() {
      // EMPTY
    }
  }
}
