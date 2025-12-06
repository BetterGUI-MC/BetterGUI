package me.hsgamer.bettergui.builder;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;

import java.io.File;

/**
 * The config builder
 */
public class ConfigBuilder extends FunctionalMassBuilder<File, Config> implements Loadable {
  public ConfigBuilder() {
  }

  @Override
  public void load() {
    register(BukkitConfig::new, "yml", "yaml");
  }

  @Override
  public void disable() {
    clear();
  }

  @Override
  protected String getType(File input) {
    String name = input.getName();
    int index = name.lastIndexOf('.');
    if (index < 0) return "";
    return name.substring(index + 1);
  }
}
