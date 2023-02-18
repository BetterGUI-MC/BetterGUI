package me.hsgamer.bettergui.manager;

import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.addon.object.Addon;
import me.hsgamer.hscore.bukkit.addon.PluginAddonManager;
import me.hsgamer.hscore.bukkit.utils.BukkitUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.expansion.common.ExpansionClassLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class ExtraAddonManager extends PluginAddonManager {
  private static final Comparator<Map.Entry<String, ExpansionClassLoader>> dependComparator = (entry1, entry2) -> {
    ExpansionClassLoader loader1 = entry1.getValue();
    String name1 = entry1.getKey();
    List<String> depends1 = getDepends(loader1);
    List<String> softDepends1 = getSoftDepends(loader1);

    ExpansionClassLoader loader2 = entry2.getValue();
    String name2 = entry2.getKey();
    List<String> depends2 = getDepends(loader2);
    List<String> softDepends2 = getSoftDepends(loader2);

    depends1 = depends1 == null ? Collections.emptyList() : depends1;
    softDepends1 = softDepends1 == null ? Collections.emptyList() : softDepends1;

    depends2 = depends2 == null ? Collections.emptyList() : depends2;
    softDepends2 = softDepends2 == null ? Collections.emptyList() : softDepends2;

    if (depends1.contains(name2) || softDepends1.contains(name2)) {
      return 1;
    } else if (depends2.contains(name1) || softDepends2.contains(name1)) {
      return -1;
    } else {
      return 0;
    }
  };

  public ExtraAddonManager(JavaPlugin javaPlugin) {
    super(javaPlugin);
    setSortAndFilterFunction(this::sortAndFilter);
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

  private static List<String> getDepends(ExpansionClassLoader loader) {
    Object value = MapUtil.getIfFound(loader.getDescription().getData(), "depends", "depend");
    if (value == null) {
      return Collections.emptyList();
    }
    return CollectionUtils.createStringListFromObject(value, true);
  }

  private static List<String> getSoftDepends(ExpansionClassLoader loader) {
    Object value = MapUtil.getIfFound(loader.getDescription().getData(), "soft-depend", "softdepend", "soft-depends", "softdepends");
    if (value == null) {
      return Collections.emptyList();
    }
    return CollectionUtils.createStringListFromObject(value, true);
  }

  private static List<String> getPluginDepends(Addon addon) {
    Object value = MapUtil.getIfFound(addon.getDescription().getData(), "plugin-depend", "plugin", "plugin-depends", "plugins");
    if (value == null) {
      return Collections.emptyList();
    }
    return CollectionUtils.createStringListFromObject(value, true);
  }

  private @NotNull Map<String, ExpansionClassLoader> sortAndFilter(@NotNull Map<String, ExpansionClassLoader> original) {
    Map<String, ExpansionClassLoader> sorted = new LinkedHashMap<>();
    Map<String, ExpansionClassLoader> remaining = new HashMap<>();

    // Start with addons with no dependency and get the remaining
    Consumer<Map.Entry<String, ExpansionClassLoader>> consumer = entry -> {
      ExpansionClassLoader addon = entry.getValue();
      if (Validate.isNullOrEmpty(getDepends(addon)) && Validate.isNullOrEmpty(getSoftDepends(addon))) {
        sorted.put(entry.getKey(), entry.getValue());
      } else {
        remaining.put(entry.getKey(), entry.getValue());
      }
    };
    original.entrySet().forEach(consumer);

    // Organize the remaining
    if (remaining.isEmpty()) {
      return sorted;
    }

    remaining.entrySet().stream().filter(stringAddonEntry -> {
      ExpansionClassLoader addon = stringAddonEntry.getValue();
      String name = stringAddonEntry.getKey();

      // Check if the required dependencies are loaded
      List<String> depends = getDepends(addon);
      if (Validate.isNullOrEmpty(depends)) {
        return true;
      }

      for (String depend : depends) {
        if (!original.containsKey(depend)) {
          getLogger().warning("Missing dependency for " + name + ": " + depend);
          return false;
        }
      }

      return true;
    }).sorted(dependComparator).forEach(entry -> sorted.put(entry.getKey(), entry.getValue()));

    return sorted;
  }

  @Override
  protected boolean onAddonLoading(@NotNull Addon addon) {
    List<String> requiredPlugins = getPluginDepends(addon);
    if (Validate.isNullOrEmpty(requiredPlugins)) {
      return true;
    }

    List<String> missing = BukkitUtils.getMissingDepends(requiredPlugins);
    if (!missing.isEmpty()) {
      getLogger().warning(() -> "Missing plugin dependency for " + addon.getDescription().getName() + ": " + Arrays.toString(missing.toArray()));
      return false;
    }

    return true;
  }

  /**
   * Get addon count
   *
   * @return the addon count
   */
  public Map<String, Integer> getAddonCount() {
    Map<String, Integer> map = new HashMap<>();
    getEnabledExpansions().keySet().forEach(s -> map.put(s, 1));
    return map;
  }
}
