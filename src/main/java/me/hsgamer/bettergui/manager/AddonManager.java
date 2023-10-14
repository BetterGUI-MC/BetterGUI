package me.hsgamer.bettergui.manager;

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

public class AddonManager extends ExpansionManager {
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
      }
    });
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
}
