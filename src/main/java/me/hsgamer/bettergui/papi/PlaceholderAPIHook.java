package me.hsgamer.bettergui.papi;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.bettergui.BetterGUI;
import org.bukkit.Bukkit;

public class PlaceholderAPIHook implements Loadable {
  private final BetterGUI plugin;
  private Runnable disableRunnable;

  public PlaceholderAPIHook(BetterGUI plugin) {
    this.plugin = plugin;
  }

  @Override
  public void enable() {
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      MenuPlaceholderExpansion expansion = new MenuPlaceholderExpansion(plugin);
      expansion.register();
      this.disableRunnable = expansion::unregister;
    }
  }

  @Override
  public void disable() {
    if (disableRunnable != null) {
      disableRunnable.run();
    }
    disableRunnable = null;
  }
}
