package me.hsgamer.bettergui.builder;

import me.hsgamer.hscore.builder.MassBuilder;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;

import java.io.File;
import java.util.Optional;
import java.util.function.Function;

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
   * @param type    the file type (extension)
   */
  public void register(Function<File, Config> creator, String... type) {
    register(file -> {
      String name = file.getName();
      int index = name.lastIndexOf('.');
      if (index < 0) return Optional.empty();

      String extension = name.substring(index + 1);
      boolean isMatched = false;
      for (String s : type) {
        if (s.equalsIgnoreCase(extension)) {
          isMatched = true;
          break;
        }
      }

      return isMatched ? Optional.of(creator.apply(file)) : Optional.empty();
    });
  }
}
