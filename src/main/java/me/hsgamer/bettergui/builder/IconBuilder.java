package me.hsgamer.bettergui.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MainConfig.DefaultConfig;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.icon.AnimatedIcon;
import me.hsgamer.bettergui.object.icon.DummyIcon;
import me.hsgamer.bettergui.object.icon.ListIcon;
import me.hsgamer.bettergui.object.icon.SimpleIcon;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class IconBuilder {

  private static final Map<String, Class<? extends Icon>> iconTypes = new CaseInsensitiveStringMap<>();

  static {
    register("dummy", DummyIcon.class);
    register("simple", SimpleIcon.class);
    register("animated", AnimatedIcon.class);
    register("list", ListIcon.class);
  }

  private IconBuilder() {

  }

  public static void register(String type, Class<? extends Icon> clazz) {
    iconTypes.put(type, clazz);
  }

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

  public static Icon getIcon(Menu menu, ConfigurationSection section) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section.getValues(false));
    if (keys.containsKey("type")) {
      String type = (String) keys.get("type");
      if (iconTypes.containsKey(type)) {
        return getIcon(menu, section, iconTypes.get(type));
      }
    }
    return getIcon(menu, section, iconTypes.getOrDefault(BetterGUI.getInstance().getMainConfig().get(DefaultConfig.DEFAULT_ICON_TYPE), SimpleIcon.class));
  }

  public static <T extends Icon> T getIcon(Menu menu, ConfigurationSection section,
      Class<T> tClass) {
    try {
      T icon = tClass.getDeclaredConstructor(String.class, Menu.class)
          .newInstance(section.getName(), menu);
      icon.setFromSection(section);
      return icon;
    } catch (Exception ex) {
      // Checked at startup
    }
    return null;
  }

  public static List<Integer> getSlots(ConfigurationSection section) {
    List<Integer> slots = new ArrayList<>();
    Map<String, Object> map = section.getValues(false);

    if (map.containsKey(SlotSetting.X) || map.containsKey(SlotSetting.Y)) {
      int x = 1;
      int y = 1;
      if (map.containsKey(SlotSetting.X)) {
        x = (int) map.get(SlotSetting.X);
      }
      if (map.containsKey(SlotSetting.Y)) {
        y = (int) map.get(SlotSetting.Y);
      }
      slots.add(y > 1 ? y * 9 + x - 1 : x - 1);
    } else if (map.containsKey(SlotSetting.SLOT)) {
      String input = (String) map.get(SlotSetting.SLOT);
      for (String string : input.split(",")) {
        if (Validate.isValidPositiveInteger(string)) {
          slots.add(Integer.parseInt(string));
        } else {
          String[] split = string.split("-", 2);
          if (Validate.isValidPositiveInteger(split[0]) && Validate
              .isValidPositiveInteger(split[1])) {
            int s1 = Integer.parseInt(split[0]);
            int s2 = Integer.parseInt(split[1]);
            int start = Math.min(s1, s2);
            int end = Math.max(s1, s2);
            for (int i = start; i <= end; i++) {
              slots.add(i);
            }
          }
        }
      }
    }

    return slots;
  }

  private static class SlotSetting {

    static final String X = "position-x";
    static final String Y = "position-y";
    static final String SLOT = "slot";
  }
}
