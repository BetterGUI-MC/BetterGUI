package me.hsgamer.bettergui.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.replacer.LookupStringReplacer;
import me.hsgamer.hscore.bukkit.variable.BukkitVariableBundle;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.variable.CommonVariableBundle;
import me.hsgamer.hscore.variable.VariableBundle;

import java.util.ArrayList;
import java.util.List;

public class VariableManager extends me.hsgamer.hscore.variable.VariableManager implements Loadable {
  private final List<VariableBundle> bundles = new ArrayList<>();

  public VariableManager(BetterGUI plugin) {
    register("menu_", (LookupStringReplacer) original -> {
      MenuManager manager = plugin.get(MenuManager.class);
      String found = null;
      for (String name : manager.getMenuNames()) {
        if (original.startsWith(name)) {
          if (found == null || name.length() > found.length()) {
            found = name;
          }
        }
      }
      if (found == null) {
        return null;
      }
      Menu menu = manager.getMenu(found);
      if (menu == null) {
        return null;
      }
      return Pair.of(menu.getStringReplacer(), original.substring(found.length()));
    });
  }

  @Override
  public void load() {
    bundles.add(new CommonVariableBundle(this));
    bundles.add(new BukkitVariableBundle(this));
  }

  @Override
  public void disable() {
    bundles.forEach(VariableBundle::unregisterAll);
    bundles.clear();
  }
}
