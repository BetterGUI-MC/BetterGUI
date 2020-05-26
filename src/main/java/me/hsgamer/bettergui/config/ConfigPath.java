package me.hsgamer.bettergui.config;

public class ConfigPath<T> {

  private final Class<T> clazz;
  private final String path;
  private final T def;
  private PluginConfig config;

  public ConfigPath(Class<T> clazz, String path, T def) {
    this.clazz = clazz;
    this.path = path;
    this.def = def;
  }

  public void setConfig(PluginConfig config) {
    this.config = config;
    config.getConfig().addDefault(path, def);
  }

  public T getValue() {
    if (config == null) {
      return def;
    }
    return config.get(clazz, path, def);
  }
}
