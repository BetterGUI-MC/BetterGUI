package me.hsgamer.bettergui.manager;

import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.bukkit.addon.PluginAddonManager;
import me.hsgamer.hscore.bukkit.utils.BukkitUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.expansion.common.ExpansionClassLoader;
import me.hsgamer.hscore.expansion.common.ExpansionState;
import me.hsgamer.hscore.expansion.common.exception.InvalidExpansionDescriptionException;
import me.hsgamer.hscore.expansion.extra.manager.DependableExpansionSortAndFilter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ExtraAddonManager extends PluginAddonManager {
  public ExtraAddonManager(JavaPlugin javaPlugin) {
    super(javaPlugin);
    addStateListener((loader, state) -> {
      if (state == ExpansionState.LOADING) {
        onLoading(loader);
      }
    });
    setSortAndFilterFunction(new DependableExpansionSortAndFilter() {
      @Override
      public List<String> getDependencies(ExpansionClassLoader loader) {
        Object value = MapUtil.getIfFound(loader.getDescription().getData(), "depends", "depend");
        if (value == null) {
          return Collections.emptyList();
        }
        return CollectionUtils.createStringListFromObject(value, true);
      }

      @Override
      public List<String> getSoftDependencies(ExpansionClassLoader loader) {
        Object value = MapUtil.getIfFound(loader.getDescription().getData(), "plugin-depend", "plugin", "plugin-depends", "plugins");
        if (value == null) {
          return Collections.emptyList();
        }
        return CollectionUtils.createStringListFromObject(value, true);
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
  public static List<String> getAuthors(ExpansionClassLoader loader) {
    Object value = MapUtil.getIfFound(loader.getDescription().getData(), "authors", "author");
    if (value == null) {
      return Collections.emptyList();
    }
    return CollectionUtils.createStringListFromObject(value, true);
  }

  /**
   * Get the description of the loader
   *
   * @param loader the loader
   *
   * @return the description
   */
  public static String getDescription(ExpansionClassLoader loader) {
    Object value = loader.getDescription().getData().get("description");
    return Objects.toString(value, "");
  }

  private static List<String> getPluginDepends(ExpansionClassLoader loader) {
    Object value = MapUtil.getIfFound(loader.getDescription().getData(), "plugin-depend", "plugin", "plugin-depends", "plugins");
    if (value == null) {
      return Collections.emptyList();
    }
    return CollectionUtils.createStringListFromObject(value, true);
  }

  private void onLoading(@NotNull ExpansionClassLoader loader) {
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
}
