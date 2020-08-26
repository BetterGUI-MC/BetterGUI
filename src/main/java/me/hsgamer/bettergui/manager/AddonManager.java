package me.hsgamer.bettergui.manager;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.object.addon.AdditionalAddonSettings;
import me.hsgamer.hscore.bukkit.addon.object.Addon;
import me.hsgamer.hscore.common.Validate;

public final class AddonManager extends me.hsgamer.hscore.bukkit.addon.AddonManager {

  private static final Comparator<Map.Entry<String, Addon>> DEPEND_COMPARATOR = (entry1, entry2) -> {
    Addon addon1 = entry1.getValue();
    String name1 = entry1.getKey();
    List<String> depends1 = AdditionalAddonSettings.DEPEND.get(addon1);
    List<String> softDepends1 = AdditionalAddonSettings.SOFT_DEPEND.get(addon1);

    Addon addon2 = entry2.getValue();
    String name2 = entry2.getKey();
    List<String> depends2 = AdditionalAddonSettings.DEPEND.get(addon2);
    List<String> softDepends2 = AdditionalAddonSettings.SOFT_DEPEND.get(addon2);

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

  public AddonManager(BetterGUI plugin) {
    super(plugin);
  }

  @Override
  protected Map<String, Addon> sortAndFilter(Map<String, Addon> original) {
    Map<String, Addon> sorted = new LinkedHashMap<>();
    Map<String, Addon> remaining = new HashMap<>();

    // Start with addons with no dependency and get the remaining
    Consumer<Map.Entry<String, Addon>> consumer = entry -> {
      Addon addon = entry.getValue();
      if (Validate.isNullOrEmpty(AdditionalAddonSettings.DEPEND.get(addon)) && Validate
          .isNullOrEmpty(AdditionalAddonSettings.SOFT_DEPEND.get(addon))) {
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
      List<String> depends = AdditionalAddonSettings.DEPEND.get(addon);
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
    List<String> requiredPlugins = AdditionalAddonSettings.PLUGIN_DEPEND.get(addon);
    if (Validate.isNullOrEmpty(requiredPlugins)) {
      return true;
    }

    List<String> missing = Validate.getMissingDepends(requiredPlugins);
    if (!missing.isEmpty()) {
      getPlugin().getLogger().warning(
          () -> "Missing plugin dependency for " + addon.getDescription().getName() + ": " + Arrays
              .toString(missing.toArray()));
      return false;
    }

    return true;
  }
}
