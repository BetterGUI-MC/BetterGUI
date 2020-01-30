package me.hsgamer.bettergui.config.impl;

import me.hsgamer.bettergui.config.PluginConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class MainConfig extends PluginConfig {

  public MainConfig(JavaPlugin plugin) {
    super(plugin, "config.yml");
    for (DefaultConfig defaultConfig : DefaultConfig.values()) {
      getConfig().set(defaultConfig.path, defaultConfig.def);
    }
    saveConfig();
  }

  @SuppressWarnings("unchecked")
  public <T> T get(DefaultConfig defaultConfig) {
    return get((Class<T>) defaultConfig.classType, defaultConfig.path, defaultConfig.def);
  }

  public enum DefaultConfig {
    USE_HOVER_EVENT(boolean.class, "use-hover-event", true)
    ;
    Class<?> classType;
    String path;
    Object def;
    DefaultConfig(Class<?> classType, String path, Object def) {
      this.classType = classType;
      this.path = path;
      this.def = def;
    }
  }
}
