package me.hsgamer.bettergui.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MainConfig;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.icon.AnimatedIcon;
import me.hsgamer.bettergui.object.icon.DummyIcon;
import me.hsgamer.bettergui.object.icon.ListIcon;
import me.hsgamer.bettergui.object.icon.RawIcon;
import me.hsgamer.bettergui.object.icon.SimpleIcon;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.configuration.ConfigurationSection;

public final class IconBuilder {

  private static final Map<String, Class<? extends Icon>> iconTypes = new CaseInsensitiveStringMap<>();

  static {
    register("dummy", DummyIcon.class);
    register("simple", SimpleIcon.class);
    register("animated", AnimatedIcon.class);
    register("list", ListIcon.class);
    register("raw", RawIcon.class);
  }

  private IconBuilder() {

  }

  /**
   * Register new Icon type
   *
   * @param type  name of the type
   * @param clazz the class
   */
  public static void register(String type, Class<? extends Icon> clazz) {
    iconTypes.put(type, clazz);
  }

  /**
   * Check the integrity of the classes
   */
  public static void checkClass() {
    for (Class<? extends Icon> clazz : iconTypes.values()) {
      try {
        clazz.getDeclaredConstructor(String.class, Menu.class).newInstance("", null);
      } catch (Exception ex) {
        BetterGUI.getInstance().getLogger()
            .log(Level.WARNING, ex, () -> "There is an unknown error on " + clazz.getSimpleName()
                + ". The icon will be ignored");
      }
    }
  }

  public static Icon getIcon(Menu<?> menu, ConfigurationSection section) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section.getValues(false));
    if (keys.containsKey("type")) {
      String type = String.valueOf(keys.get("type"));
      if (iconTypes.containsKey(type)) {
        return getIcon(menu, section, iconTypes.get(type));
      }
    }
    return getIcon(menu, section, iconTypes
        .getOrDefault(MainConfig.DEFAULT_ICON_TYPE.getValue(), SimpleIcon.class));
  }

  public static <T extends Icon> T getIcon(Menu<?> menu, ConfigurationSection section,
      Class<T> tClass) {
    try {
      T icon = tClass.getDeclaredConstructor(String.class, Menu.class)
          .newInstance(section.getName(), menu);
      icon.setFromSection(section);
      return icon;
    } catch (Exception ex) {
      BetterGUI.getInstance().getLogger().log(Level.WARNING, ex, () ->
          "Something wrong when creating the icon '" + section.getName() + "' in the menu '" + menu
              .getName() + "'");
    }
    return null;
  }

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
