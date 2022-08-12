package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.menu.AddonMenu;
import me.hsgamer.bettergui.menu.ArgumentMenu;
import me.hsgamer.bettergui.menu.PredicateMenu;
import me.hsgamer.bettergui.menu.SimpleMenu;
import me.hsgamer.hscore.builder.MassBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.config.Config;

import java.util.Map;
import java.util.Objects;
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
    register(ArgumentMenu::new, "argument", "args", "arguments");
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
    register(new Element<Config, Menu>() {
      @Override
      public boolean canBuild(Config input) {
        Map<String, Object> keys = new CaseInsensitiveStringMap<>(input.getNormalizedValues(true));
        String menu = Objects.toString(keys.get("menu-settings.menu-type"), BetterGUI.getInstance().getMainConfig().defaultMenuType);
        for (String s : type) {
          if (menu.equalsIgnoreCase(s)) {
            return true;
          }
        }
        return false;
      }

      @Override
      public Menu build(Config input) {
        return creator.apply(input);
      }
    });
  }
}
