package me.hsgamer.bettergui.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import io.github.projectunified.minelib.plugin.postenable.PostEnable;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.hscore.bukkit.expansion.BukkitConfigExpansionDescriptionLoader;
import me.hsgamer.hscore.bukkit.utils.BukkitUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.expansion.common.ExpansionClassLoader;
import me.hsgamer.hscore.expansion.common.ExpansionManager;
import me.hsgamer.hscore.expansion.common.ExpansionState;
import me.hsgamer.hscore.expansion.common.exception.InvalidExpansionDescriptionException;
import me.hsgamer.hscore.expansion.extra.manager.DependableExpansionSortAndFilter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class AddonManager extends ExpansionManager implements Loadable, PostEnable {
  private final BetterGUI plugin;

  public AddonManager(BetterGUI plugin) {
    super(new File(plugin.getDataFolder(), "addon"), new BukkitConfigExpansionDescriptionLoader("addon.yml"), plugin.getClass().getClassLoader());
    this.plugin = plugin;
    setSortAndFilterFunction(new DependableExpansionSortAndFilter() {
      @Override
      public List<String> getDependencies(ExpansionClassLoader loader) {
        return CollectionUtils.createStringListFromObject(MapUtils.getIfFound(loader.getDescription().getData(), "depend", "depends", "dependencies"));
      }

      @Override
      public List<String> getSoftDependencies(ExpansionClassLoader loader) {
        return CollectionUtils.createStringListFromObject(MapUtils.getIfFound(loader.getDescription().getData(), "softdepend", "softdepends", "soft-dependencies"));
      }
    });
    addStateListener((loader, state) -> {
      if (state == ExpansionState.LOADING) {
        checkPluginDepends(loader);
      } else if (state == ExpansionState.ERROR) {
        plugin.getLogger().log(Level.WARNING, "There is an error when loading an addon: " + loader.getDescription().getName(), loader.getThrowable());
      } else if (state == ExpansionState.ENABLED) {
        plugin.getLogger().log(Level.INFO, "Enabled " + loader.getDescription().getName() + " " + loader.getDescription().getVersion());
      } else if (state == ExpansionState.DISABLED) {
        plugin.getLogger().log(Level.INFO, "Disabled " + loader.getDescription().getName() + " " + loader.getDescription().getVersion());
      }
    });
    setExceptionHandler(throwable -> plugin.getLogger().log(Level.SEVERE, "There is an error when handling an addon", throwable));
  }

  /**
   * Get the authors of the loader
   *
   * @param loader the loader
   *
   * @return the authors
   */
  @NotNull
  public static List<@NotNull String> getAuthors(@NotNull ExpansionClassLoader loader) {
    Object value = MapUtils.getIfFound(loader.getDescription().getData(), "authors", "author");
    return CollectionUtils.createStringListFromObject(value, true);
  }

  /**
   * Get the description of the loader
   *
   * @param loader the loader
   *
   * @return the description
   */
  @NotNull
  public static String getDescription(@NotNull ExpansionClassLoader loader) {
    Object value = loader.getDescription().getData().get("description");
    return Objects.toString(value, "");
  }

  @NotNull
  private static List<@NotNull String> getPluginDepends(@NotNull ExpansionClassLoader loader) {
    Object value = MapUtils.getIfFound(loader.getDescription().getData(), "plugin-depend", "plugin", "plugin-depends", "plugins");
    return CollectionUtils.createStringListFromObject(value, true);
  }

  private void checkPluginDepends(@NotNull ExpansionClassLoader loader) {
    List<String> requiredPlugins = getPluginDepends(loader);
    if (Validate.isNullOrEmpty(requiredPlugins)) return;

    List<String> missing = BukkitUtils.getMissingDepends(requiredPlugins);
    if (!missing.isEmpty()) {
      throw new InvalidExpansionDescriptionException("Missing plugin dependency for " + loader.getDescription().getName() + ": " + Arrays.toString(missing.toArray()));
    }
  }

  /**
   * Get expansion count
   *
   * @return the expansion count
   */
  public Map<String, Integer> getExpansionCount() {
    Map<String, Integer> map = new HashMap<>();
    getEnabledExpansions().keySet().forEach(s -> map.put(s, 1));
    return map;
  }

  /**
   * Get the plugin
   *
   * @return the plugin
   */
  public BetterGUI getPlugin() {
    return plugin;
  }

  @Override
  public void enable() {
    loadExpansions();
  }

  @Override
  public void postEnable() {
    enableExpansions();
  }

  @Override
  public void disable() {
    disableExpansions();
    clearExpansions();
  }
}
