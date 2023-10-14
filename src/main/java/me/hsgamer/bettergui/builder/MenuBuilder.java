package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.menu.AddonMenu;
import me.hsgamer.bettergui.menu.PredicateMenu;
import me.hsgamer.bettergui.menu.SimpleMenu;
import me.hsgamer.hscore.builder.MassBuilder;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathString;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * The menu builder
 */
public final class MenuBuilder extends MassBuilder<Config, Menu> {
  /**
   * The instance of the menu builder
   */
  public static final MenuBuilder INSTANCE = new MenuBuilder();

  private MenuBuilder() {
    register(SimpleMenu::new, "simple");
    register(AddonMenu::new, "addon");
    register(PredicateMenu::new, "predicate");
  }

  /**
   * Register a new menu creator
   *
   * @param creator the creator
   * @param type    the type
   */
  public void register(Function<Config, Menu> creator, String... type) {
    register(input -> {
      String menu = "simple";
      for (Map.Entry<PathString, Object> entry : input.getNormalizedValues(true).entrySet()) {
        String[] path = entry.getKey().getPath();
        if (path.length == 2 && path[0].equalsIgnoreCase("menu-settings") && path[1].equalsIgnoreCase("menu-type")) {
          menu = Objects.toString(entry.getValue(), "simple");
          break;
        }
      }

      for (String s : type) {
        if (menu.equalsIgnoreCase(s)) {
          return Optional.of(creator.apply(input));
        }
      }
      return Optional.empty();
    });
  }
}
