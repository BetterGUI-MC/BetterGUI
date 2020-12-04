package me.hsgamer.bettergui.manager;

import me.hsgamer.hscore.common.interfaces.StringReplacer;
import me.hsgamer.hscore.variable.VariableManager;

import java.util.HashSet;
import java.util.Set;

/**
 * The variable manager for the plugin
 */
public class PluginVariableManager {
  private static final Set<String> pluginVariables = new HashSet<>();

  private PluginVariableManager() {
    // EMPTY
  }

  /**
   * Register the variable
   *
   * @param prefix         the prefix
   * @param stringReplacer the string replacer
   */
  public static void register(String prefix, StringReplacer stringReplacer) {
    pluginVariables.add(prefix);
    VariableManager.register(prefix, stringReplacer);
  }

  /**
   * Unregister the variable
   *
   * @param prefix the prefix
   */
  public static void unregister(String prefix) {
    pluginVariables.remove(prefix);
    VariableManager.unregister(prefix);
  }

  /**
   * Unregister all plugin variables
   */
  public static void unregisterAll() {
    pluginVariables.forEach(VariableManager::unregister);
    pluginVariables.clear();
  }
}
