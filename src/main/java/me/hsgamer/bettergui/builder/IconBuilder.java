package me.hsgamer.bettergui.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
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
import me.hsgamer.bettergui.util.TestCase;
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class IconBuilder {

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
            .log(Level.WARNING, "There is an unknown error on " + clazz.getSimpleName()
                + ". The icon will be ignored", ex);
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
      BetterGUI.getInstance().getLogger().log(Level.WARNING,
          "Something wrong when creating the icon '" + section.getName() + "' in the menu '" + menu
              .getName() + "'", ex);
    }
    return null;
  }

  public static List<Integer> getSlots(ConfigurationSection section) {
    List<Integer> slots = new ArrayList<>();
    Map<String, Object> map = new CaseInsensitiveStringMap<>(section.getValues(false));

    TestCase.create(map)
        .setPredicate(
            stringObjectMap -> stringObjectMap.containsKey(SlotSetting.X) || stringObjectMap
                .containsKey(SlotSetting.Y))
        .setSuccessConsumer(stringObjectMap -> {
          int x = 1;
          int y = 1;
          if (stringObjectMap.containsKey(SlotSetting.X)) {
            x = Integer.parseInt(String.valueOf(map.get(SlotSetting.X)));
          }
          if (stringObjectMap.containsKey(SlotSetting.Y)) {
            y = Integer.parseInt(String.valueOf(map.get(SlotSetting.Y)));
          }
          slots.add((y - 1) * 9 + x - 1);
        })
        .setFailNextTestCase(stringObjectMap -> TestCase.create(stringObjectMap)
            .setPredicate(stringObjectMap1 -> stringObjectMap1.containsKey(SlotSetting.SLOT))
            .setSuccessConsumer(stringObjectMap1 -> {
              String input = String.valueOf(stringObjectMap1.get(SlotSetting.SLOT));
              TestCase<String> testCase = new TestCase<String>()
                  .setBeforeTestOperator(String::trim)
                  .setPredicate(Validate::isValidInteger)
                  .setFailConsumer(s -> {
                    String[] split = s.split("-", 2);
                    Optional<BigDecimal> s1 = Validate.getNumber(split[0]);
                    Optional<BigDecimal> s2 = Validate.getNumber(split[1]);
                    if (s1.isPresent() && s2.isPresent()) {
                      int start = Math.min(s1.get().intValue(), s2.get().intValue());
                      int end = Math.max(s1.get().intValue(), s2.get().intValue());
                      for (int i = start; i <= end; i++) {
                        slots.add(i);
                      }
                    }
                  })
                  .setSuccessConsumer(s -> slots.add(Integer.parseInt(s)));
              for (String s : input.split(",")) {
                testCase.setTestObject(s).test();
              }
            }))
        .test();

    return slots;
  }

  private static class SlotSetting {

    static final String X = "position-x";
    static final String Y = "position-y";
    static final String SLOT = "slot";
  }
}
