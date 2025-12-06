package me.hsgamer.bettergui.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.variable.BukkitVariableBundle;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.variable.CommonVariableBundle;
import me.hsgamer.hscore.variable.VariableBundle;

import java.util.ArrayList;
import java.util.List;

public class VariableManager extends me.hsgamer.hscore.variable.VariableManager implements Loadable {
  private final List<VariableBundle> bundles = new ArrayList<>();

  public VariableManager(BetterGUI plugin) {
    register("menu_", StringReplacer.of((original, uuid) -> {
      String[] split = original.split("_", 2);
      String menuName = split[0].trim();
      String variable = split.length > 1 ? split[1].trim() : "";
      Menu menu = plugin.get(MenuManager.class).getMenu(menuName);
      if (menu == null) {
        return null;
      }
      return menu.getVariableManager().setVariables(StringReplacerApplier.normalizeQuery(variable), uuid);
    }));
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
