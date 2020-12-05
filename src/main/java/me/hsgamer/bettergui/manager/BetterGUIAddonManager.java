package me.hsgamer.bettergui.manager;

import me.hsgamer.hscore.addon.object.Addon;
import me.hsgamer.hscore.addon.object.AddonPath;
import me.hsgamer.hscore.bukkit.addon.PluginAddonManager;
import me.hsgamer.hscore.bukkit.config.PluginYamlProvider;
import me.hsgamer.hscore.bukkit.utils.BukkitUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.config.ConfigProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Consumer;

public class BetterGUIAddonManager extends PluginAddonManager {
  private static final Comparator<Map.Entry<String, Addon>> DEPEND_COMPARATOR = (entry1, entry2) -> {
    Addon addon1 = entry1.getValue();
    String name1 = entry1.getKey();
    List<String> depends1 = Setting.DEPEND.get(addon1);
    List<String> softDepends1 = Setting.SOFT_DEPEND.get(addon1);

    Addon addon2 = entry2.getValue();
    String name2 = entry2.getKey();
    List<String> depends2 = Setting.DEPEND.get(addon2);
    List<String> softDepends2 = Setting.SOFT_DEPEND.get(addon2);

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

  /**
   * Create a new addon manager
   *
   * @param javaPlugin the plugin
   */
  public BetterGUIAddonManager(JavaPlugin javaPlugin) {
    super(javaPlugin);
  }

  @Override
  public String getAddonConfigFileName() {
    return "addon.yml";
  }

  @Override
  protected ConfigProvider<?> getConfigProvider() {
    return new PluginYamlProvider();
  }

  /**
   * Get addon count
   *
   * @return the addon count
   */
  public Map<String, Integer> getAddonCount() {
    Map<String, Integer> map = new HashMap<>();
    getLoadedAddons().keySet().forEach(s -> map.put(s, 1));
    return map;
  }

  @Override
  protected Map<String, Addon> sortAndFilter(Map<String, Addon> original) {
    Map<String, Addon> sorted = new LinkedHashMap<>();
    Map<String, Addon> remaining = new HashMap<>();

    // Start with addons with no dependency and get the remaining
    Consumer<Map.Entry<String, Addon>> consumer = entry -> {
      Addon addon = entry.getValue();
      if (Validate.isNullOrEmpty(Setting.DEPEND.get(addon)) && Validate.isNullOrEmpty(Setting.SOFT_DEPEND.get(addon))) {
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
      Addon addon = stringAddonEntry.getValue();
      String name = stringAddonEntry.getKey();

      // Check if the required dependencies are loaded
      List<String> depends = Setting.DEPEND.get(addon);
      if (Validate.isNullOrEmpty(depends)) {
        return true;
      }

      for (String depend : depends) {
        if (!original.containsKey(depend)) {
          getPlugin().getLogger().warning("Missing dependency for " + name + ": " + depend);
          return false;
        }
      }

      return true;
    }).sorted(DEPEND_COMPARATOR).forEach(entry -> sorted.put(entry.getKey(), entry.getValue()));

    return sorted;
  }

  @Override
  protected boolean onAddonLoading(Addon addon) {
    List<String> requiredPlugins = Setting.PLUGIN_DEPEND.get(addon);
    if (Validate.isNullOrEmpty(requiredPlugins)) {
      return true;
    }

    List<String> missing = BukkitUtils.getMissingDepends(requiredPlugins);
    if (!missing.isEmpty()) {
      getPlugin().getLogger().warning(() -> "Missing plugin dependency for " + addon.getDescription().getName() + ": " + Arrays.toString(missing.toArray()));
      return false;
    }

    return true;
  }

  public static class Setting {
    public static final AddonPath<List<String>> AUTHORS = new AddonPath<List<String>>("authors", false) {
      @Override
      public List<String> convertType(Object object) {
        return CollectionUtils.createStringListFromObject(object, true);
      }
    };
    public static final AddonPath<String> DESCRIPTION = new AddonPath<String>("description", false) {
      @Override
      public String convertType(Object object) {
        return String.valueOf(object);
      }
    };
    public static final AddonPath<List<String>> DEPEND = new AddonPath<List<String>>("depend", false) {
      @Override
      public List<String> convertType(Object object) {
        return CollectionUtils.createStringListFromObject(object, true);
      }
    };
    public static final AddonPath<List<String>> SOFT_DEPEND = new AddonPath<List<String>>("soft-depend", false) {
      @Override
      public List<String> convertType(Object object) {
        return CollectionUtils.createStringListFromObject(object, true);
      }
    };
    public static final AddonPath<List<String>> PLUGIN_DEPEND = new AddonPath<List<String>>("plugin-depend", false) {
      @Override
      public List<String> convertType(Object object) {
        return CollectionUtils.createStringListFromObject(object, true);
      }
    };

    private Setting() {
      // EMPTY
    }
  }
}
