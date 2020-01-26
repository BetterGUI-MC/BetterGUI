package me.hsgamer.bettergui.builder;

import java.util.Map;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.menu.DummyMenu;
import me.hsgamer.bettergui.object.menu.SimpleMenu;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import org.bukkit.configuration.file.FileConfiguration;

public class MenuBuilder {

  private static final Map<String, Class<? extends Menu>> menuTypes = new CaseInsensitiveStringMap<>();

  static {
    register("dummy", DummyMenu.class);
    register("simple", SimpleMenu.class);
  }

  public static void register(String type, Class<? extends Menu> clazz) {
    menuTypes.put(type, clazz);
  }

  public static void checkClass() {
    for (Class<? extends Menu> clazz : menuTypes.values()) {
      try {
        clazz.getDeclaredConstructor(String.class).newInstance("");
      } catch (Exception ex) {
        BetterGUI.getInstance().getLogger()
            .log(Level.WARNING, "There is an unknown error on " + clazz.getSimpleName()
                + ". The menu type will be ignored", ex);
      }
    }
  }

  public static Menu getMenu(FileConfiguration file) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(file.getValues(false));
    if (keys.containsKey("menu-settings.menu-type")) {
      String type = (String) keys.get("menu-settings.menu-type");
      if (menuTypes.containsKey(type)) {
        return getMenu(file, menuTypes.get(type));
      }
    }
    return getMenu(file, SimpleMenu.class);
  }

  public static <T extends Menu> T getMenu(FileConfiguration file,
      Class<T> tClass) {
    try {
      T menu = tClass.getDeclaredConstructor(String.class)
          .newInstance(file.getName());
      menu.setFromFile(file);
      return menu;
    } catch (Exception ex) {
      // Checked at startup
    }
    return null;
  }
}
