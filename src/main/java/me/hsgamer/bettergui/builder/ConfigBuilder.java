package me.hsgamer.bettergui.builder;

import me.hsgamer.hscore.builder.MassBuilder;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;

import java.io.File;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The config builder
 */
public class ConfigBuilder extends MassBuilder<File, Config> {
  /**
   * the singleton instance
   */
  public static final ConfigBuilder INSTANCE = new ConfigBuilder();

  private ConfigBuilder() {
    register(BukkitConfig::new, "yml", "yaml");
  }

  /**
   * Register a new config creator
   *
   * @param creator the creator
   * @param checker the file checker
   */
  public void register(Function<File, Config> creator, Predicate<File> checker) {
    register(new Element<File, Config>() {
      @Override
      public boolean canBuild(File file) {
        return checker.test(file);
      }

      @Override
      public Config build(File file) {
        return creator.apply(file);
      }
    });
  }

  /**
   * Register a new config creator
   *
   * @param creator the creator
   * @param type    the file type (extension)
   */
  public void register(Function<File, Config> creator, String... type) {
    register(creator, file -> {
      String name = file.getName();
      int index = name.lastIndexOf('.');
      if (index < 0) return false;
      String extension = name.substring(index + 1);
      return Arrays.stream(type).anyMatch(s -> s.equalsIgnoreCase(extension));
    });
  }
}
