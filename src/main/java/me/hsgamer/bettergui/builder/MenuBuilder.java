package me.hsgamer.bettergui.builder;

import java.util.Map;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MainConfig;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.menu.ArgsMenu;
import me.hsgamer.bettergui.object.menu.DummyMenu;
import me.hsgamer.bettergui.object.menu.SimpleMenu;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import org.bukkit.configuration.file.FileConfiguration;

public final class MenuBuilder {

  private static final Map<String, Class<? extends Menu<?>>> menuTypes = new CaseInsensitiveStringMap<>();

  static {
    register("dummy", DummyMenu.class);
    register("simple", SimpleMenu.class);
    register("args", ArgsMenu.class);
  }

  private MenuBuilder() {

  }

  /**
   * Register new Menu type
   *
   * @param type  the name of the type
   * @param clazz the class
   */
  public static void register(String type, Class<? extends Menu<?>> clazz) {
    menuTypes.put(type, clazz);
  }

  /**
   * Check the integrity of the classes
   */
  public static void checkClass() {
    menuTypes.values().forEach(clazz -> {
      try {
        clazz.getDeclaredConstructor(String.class).newInstance("");
      } catch (Exception ex) {
        BetterGUI.getInstance().getLogger()
            .log(Level.WARNING, ex, () -> "There is an unknown error on " + clazz.getSimpleName()
                + ". The menu type will be ignored");
      }
    });
  }

  public static Menu<?> getMenu(String name, FileConfiguration file) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(file.getValues(true));
    if (keys.containsKey("menu-settings.menu-type")) {
      String type = String.valueOf(keys.get("menu-settings.menu-type"));
      if (menuTypes.containsKey(type)) {
        return getMenu(name, file, menuTypes.get(type));
      }
    }
    return getMenu(name, file, menuTypes.get(MainConfig.DEFAULT_MENU_TYPE.getValue()));
  }

  public static Menu<?> getMenu(String name, FileConfiguration file,
      Class<? extends Menu<?>> tClass) {
    try {
      Menu<?> menu = tClass.getDeclaredConstructor(String.class)
          .newInstance(name);
      menu.setFromFile(file);
      return menu;
    } catch (Exception ex) {
      BetterGUI.getInstance().getLogger()
          .log(Level.WARNING, ex, () -> "Something wrong when creating the menu '" + name + "'");
    }
    return null;
  }
}
