package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.menu.AddonMenu;
import me.hsgamer.bettergui.menu.PredicateMenu;
import me.hsgamer.bettergui.menu.SimpleMenu;
import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.hscore.config.Config;

import java.util.Map;
import java.util.Objects;

/**
 * The menu builder
 */
public final class MenuBuilder extends FunctionalMassBuilder<Config, Menu> {
  /**
   * The instance of the menu builder
   */
  public static final MenuBuilder INSTANCE = new MenuBuilder();

  private MenuBuilder() {
    register(SimpleMenu::new, "simple");
    register(AddonMenu::new, "addon");
    register(PredicateMenu::new, "predicate");
  }

  @Override
  protected String getType(Config input) {
    String type = "simple";
    for (Map.Entry<String[], Object> entry : input.getNormalizedValues(true).entrySet()) {
      String[] path = entry.getKey();
      if (path.length == 2 && path[0].equalsIgnoreCase(Menu.MENU_SETTINGS_PATH) && path[1].equalsIgnoreCase("menu-type")) {
        type = Objects.toString(entry.getValue(), "simple");
        break;
      }
    }
    return type;
  }
}
