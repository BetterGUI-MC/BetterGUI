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

/**
 * The Menu Builder
 */
public final class MenuBuilder {

  private static final Map<String, Function<String, Menu<?>>> menuTypes = new CaseInsensitiveStringMap<>();

  static {
    register(DummyMenu::new, "dummy");
    register(SimpleMenu::new, "simple");
    register(ArgsMenu::new, "args");
  }

  private MenuBuilder() {
    // EMPTY
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
   * Get the menu
   *
   * @param name the name of the menu
   * @param file the menu file
   * @return the menu
   */
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

  /**
   * Get the menu
   *
   * @param name         the name of the menu
   * @param file         the menu file
   * @param menuFunction the "create menu" function
   * @return the menu
   */
  public static Menu<?> getMenu(String name, FileConfiguration file,
      Function<String, Menu<?>> menuFunction) {
    Menu<?> menu = menuFunction.apply(name);
    menu.setFromFile(file);
    return menu;
  }
}
