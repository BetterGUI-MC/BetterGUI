package me.hsgamer.bettergui.builder;

import java.util.Map;
import java.util.function.Function;
import me.hsgamer.bettergui.config.impl.MainConfig;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.menu.ArgsMenu;
import me.hsgamer.bettergui.object.menu.DummyMenu;
import me.hsgamer.bettergui.object.menu.SimpleMenu;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import org.bukkit.configuration.file.FileConfiguration;

public final class MenuBuilder {

  private static final Map<String, Function<String, Menu<?>>> menuTypes = new CaseInsensitiveStringMap<>();

  static {
    register("dummy", DummyMenu::new);
    register("simple", SimpleMenu::new);
    register("args", ArgsMenu::new);
  }

  private MenuBuilder() {

  }

  /**
   * Register new Menu type
   *
   * @param type         the name of the type
   * @param menuFunction the "create menu" function
   */
  public static void register(String type, Function<String, Menu<?>> menuFunction) {
    menuTypes.put(type, menuFunction);
  }

  /**
   * Register new Menu type
   *
   * @param type  the name of the type
   * @param clazz the class
   * @deprecated use {@link #register(String, Function)} instead
   */
  @Deprecated
  public static void register(String type, Class<? extends Menu<?>> clazz) {
    menuTypes.put(type, s -> {
      try {
        return clazz.getDeclaredConstructor(String.class).newInstance(s);
      } catch (Exception e) {
        throw new RuntimeException("Invalid menu class");
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
      Function<String, Menu<?>> menuFunction) {
    Menu<?> menu = menuFunction.apply(name);
    menu.setFromFile(file);
    return menu;
  }
}
