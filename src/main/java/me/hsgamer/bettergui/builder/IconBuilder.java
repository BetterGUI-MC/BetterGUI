package me.hsgamer.bettergui.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.IntStream;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MainConfig.DefaultConfig;
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

  @SuppressWarnings("SuspiciousMethodCalls")
  public static Icon getIcon(Menu<?> menu, ConfigurationSection section) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section.getValues(false));
    if (keys.containsKey("type")) {
      String type = String.valueOf(keys.get("type"));
      if (iconTypes.containsKey(type)) {
        return getIcon(menu, section, iconTypes.get(type));
      }
    }
    return getIcon(menu, section, iconTypes
        .getOrDefault(BetterGUI.getInstance().getMainConfig().get(DefaultConfig.DEFAULT_ICON_TYPE),
            SimpleIcon.class));
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
      Arrays.stream(String.valueOf(map.get(SlotSetting.SLOT)).trim().split(",")).map(String::trim)
          .flatMapToInt(IconBuilder::generateSlots).forEach(slots::add);
    }
    return slots;
  }

  private static IntStream generateSlots(String input) {
    if (Validate.isValidInteger(input)) {
      return IntStream.of(Integer.parseInt(input));
    } else {
      String[] split = input.split("-", 2);
      Optional<BigDecimal> s1 = Validate.getNumber(split[0].trim());
      Optional<BigDecimal> s2 = Validate.getNumber(split[1].trim());
      if (s1.isPresent() && s2.isPresent()) {
        int start = Math.min(s1.get().intValue(), s2.get().intValue());
        int end = Math.max(s1.get().intValue(), s2.get().intValue());
        return IntStream.range(start, end + 1);
      }
    }
    return IntStream.empty();
  }

  private static class SlotSetting {

    static final String X = "position-x";
    static final String Y = "position-y";
    static final String SLOT = "slot";
  }
}
