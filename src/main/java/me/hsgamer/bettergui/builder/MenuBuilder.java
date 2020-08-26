package me.hsgamer.bettergui.builder;

import java.util.Map;
import java.util.function.Function;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.menu.ArgsMenu;
import me.hsgamer.bettergui.object.menu.DummyMenu;
import me.hsgamer.bettergui.object.menu.SimpleMenu;
import me.hsgamer.hscore.map.CaseInsensitiveStringMap;
import org.bukkit.configuration.file.FileConfiguration;

public final class MenuBuilder {

  private static final Map<String, Function<String, Menu<?>>> menuTypes = new CaseInsensitiveStringMap<>();

  static {
    register(DummyMenu::new, "dummy");
    register(SimpleMenu::new, "simple");
    register(ArgsMenu::new, "args");
  }

  private MenuBuilder() {

  }

  /**
   * Register new Menu type
   *
   * @param menuFunction the "create menu" function
   * @param type         the name of the type
   */
  public static void register(Function<String, Menu<?>> menuFunction, String... type) {
    for (String s : type) {
      menuTypes.put(s, menuFunction);
    }
  }

  /**
   * Register new Menu type
   *
   * @param type  the name of the type
   * @param clazz the class
   * @deprecated use {@link #register(Function, String...)} instead
   */
  @Deprecated
  public static void register(String type, Class<? extends Menu<?>> clazz) {
    register(s -> {
      try {
        return clazz.getDeclaredConstructor(String.class).newInstance(s);
      } catch (Exception e) {
        throw new RuntimeException("Invalid menu class");
      }
    }, type);
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
