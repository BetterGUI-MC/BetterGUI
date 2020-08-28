package me.hsgamer.bettergui.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.icon.AnimatedIcon;
import me.hsgamer.bettergui.object.icon.DummyIcon;
import me.hsgamer.bettergui.object.icon.ListIcon;
import me.hsgamer.bettergui.object.icon.RawIcon;
import me.hsgamer.bettergui.object.icon.SimpleIcon;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.map.CaseInsensitiveStringMap;
import org.bukkit.configuration.ConfigurationSection;

/**
 * The Icon Builder
 */
public final class IconBuilder {

  private static final Map<String, BiFunction<String, Menu<?>, Icon>> iconTypes = new CaseInsensitiveStringMap<>();

  static {
    register(DummyIcon::new, "dummy");
    register(SimpleIcon::new, "simple");
    register(AnimatedIcon::new, "animated");
    register(ListIcon::new, "list");
    register(RawIcon::new, "raw");
  }

  private IconBuilder() {
    // EMPTY
  }

  /**
   * Register new Icon type
   *
   * @param iconBiFunction the "create icon" function
   * @param type           the name of the type
   */
  public static void register(BiFunction<String, Menu<?>, Icon> iconBiFunction, String... type) {
    for (String s : type) {
      iconTypes.put(s, iconBiFunction);
    }
  }

  /**
   * Register new Icon type
   *
   * @param type  name of the type
   * @param clazz the class
   * @deprecated use {@link #register(BiFunction, String...)} instead
   */
  @Deprecated
  public static void register(String type, Class<? extends Icon> clazz) {
    register((s, menu) -> {
      try {
        return clazz.getDeclaredConstructor(String.class, Menu.class)
            .newInstance(s, menu);
      } catch (Exception e) {
        throw new RuntimeException("Invalid icon class");
      }
    }, type);
  }

  /**
   * Get the icon
   *
   * @param menu    the menu the icon is in
   * @param section the icon section
   * @return the icon
   */
  public static Icon getIcon(Menu<?> menu, ConfigurationSection section) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section.getValues(false));
    if (keys.containsKey("type")) {
      String type = String.valueOf(keys.get("type"));
      if (iconTypes.containsKey(type)) {
        return getIcon(menu, section, iconTypes.get(type));
      }
    }
    return getIcon(menu, section, iconTypes
        .getOrDefault(MainConfig.DEFAULT_ICON_TYPE.getValue(), SimpleIcon::new));
  }

  /**
   * Get the icon
   *
   * @param menu           the menu the icon is in
   * @param section        the icon section
   * @param iconBiFunction the "create icon" function
   * @return the icon
   */
  public static Icon getIcon(Menu<?> menu, ConfigurationSection section,
      BiFunction<String, Menu<?>, Icon> iconBiFunction) {
    Icon icon = iconBiFunction.apply(section.getName(), menu);
    icon.setFromSection(section);
    return icon;
  }

  /**
   * Get the slots for the icon
   *
   * @param section the icon section
   * @return the slots
   */
  public static List<Integer> getSlots(ConfigurationSection section) {
    List<Integer> slots = new ArrayList<>();
    Map<String, Object> map = new CaseInsensitiveStringMap<>(section.getValues(false));

    if (map.containsKey(SlotSetting.X) || map.containsKey(SlotSetting.Y)) {
      int x = 1;
      int y = 1;
      if (map.containsKey(SlotSetting.X)) {
        x = Integer.parseInt(String.valueOf(map.get(SlotSetting.X)));
      }
      if (map.containsKey(SlotSetting.Y)) {
        y = Integer.parseInt(String.valueOf(map.get(SlotSetting.Y)));
      }
      slots.add((y - 1) * 9 + x - 1);
    }
    if (map.containsKey(SlotSetting.SLOT)) {
      slots.addAll(Arrays
          .stream(String.valueOf(map.get(SlotSetting.SLOT)).split(","))
          .map(String::trim)
          .flatMap(IconBuilder::generateSlots).collect(Collectors.toList()));
    }
    return slots;
  }

  /**
   * Create a new stream of slots
   *
   * @param input the input string
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

  private static class SlotSetting {

    static final String X = "position-x";
    static final String Y = "position-y";
    static final String SLOT = "slot";
  }
}
