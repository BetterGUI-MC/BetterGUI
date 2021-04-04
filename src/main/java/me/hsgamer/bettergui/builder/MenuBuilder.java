package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.menu.ArgsMenu;
import me.hsgamer.bettergui.menu.SimpleMenu;
import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.hscore.config.Config;

import java.util.Map;
import java.util.Optional;

/**
 * The menu builder
 */
public class MenuBuilder extends Builder<String, Menu> {

  /**
   * The instance of the menu builder
   */
  public static final MenuBuilder INSTANCE = new MenuBuilder();

  private MenuBuilder() {
    registerDefaultMenus();
  }

  private void registerDefaultMenus() {
    register(SimpleMenu::new, "simple");
    register(ArgsMenu::new, "args", "argument", "arguments");
  }

  /**
   * Build the menu from the config
   *
   * @param name   the name of the menu
   * @param config the config
   *
   * @return the menu
   */
  public Menu getMenu(String name, Config config) {
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(config.getNormalizedValues(true));
    Menu menu = Optional.ofNullable(keys.get("menu-settings.menu-type"))
      .map(String::valueOf)
      .flatMap(string -> build(string, name))
      .orElseGet(() -> build(MainConfig.DEFAULT_MENU_TYPE.getValue(), name).orElse(null));
    if (menu != null) {
      menu.setFromConfig(config);
    }
    return menu;
  }
}
