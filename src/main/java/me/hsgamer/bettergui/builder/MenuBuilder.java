package me.hsgamer.bettergui.builder;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.bettergui.BetterGUI;
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
public final class MenuBuilder extends FunctionalMassBuilder<Config, Menu> implements Loadable {
  private final BetterGUI plugin;

  public MenuBuilder(BetterGUI plugin) {
    this.plugin = plugin;
  }

  @Override
  public void load() {
    register(input -> new SimpleMenu(plugin, input), "simple");
    register(input -> new AddonMenu(plugin, input), "addon");
    register(input -> new PredicateMenu(plugin, input), "predicate");
  }

  @Override
  public void disable() {
    clear();
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
