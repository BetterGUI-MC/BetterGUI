package me.hsgamer.bettergui.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.AdvancedConfigPath;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.ConfigPath;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.BooleanConfigPath;
import me.hsgamer.hscore.config.path.StringConfigPath;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public final class MainConfig extends PathableConfig {

  public static final StringConfigPath DEFAULT_MENU_TYPE = new StringConfigPath("default-menu-type", "simple");
  public static final StringConfigPath DEFAULT_BUTTON_TYPE = new StringConfigPath("default-button-type", "simple");
  public static final BooleanConfigPath METRICS = new BooleanConfigPath("metrics", true);
  public static final BooleanConfigPath MODERN_CLICK_TYPE = new BooleanConfigPath("use-modern-click-type", false);
  public static final BooleanConfigPath REPLACE_ALL_VARIABLES = new BooleanConfigPath("replace-all-variables-each-check", true);
  public static final BooleanConfigPath FORCED_UPDATE_INVENTORY = new BooleanConfigPath("forced-update-inventory", false);
  public static final BooleanConfigPath ENABLE_ALTERNATIVE_COMMAND_MANAGER = new BooleanConfigPath("alternative-command-manager.enable", false);
  public static final BooleanConfigPath ALTERNATIVE_COMMAND_MANAGER_CASE_INSENSITIVE = new BooleanConfigPath("alternative-command-manager.case-insensitive", true);
  public static final BooleanConfigPath USE_LEGACY_BUTTON = new BooleanConfigPath("use-legacy-button", true);
  public static final ConfigPath<List<String>> ALTERNATIVE_COMMAND_MANAGER_IGNORED_COMMANDS = new AdvancedConfigPath<Object, List<String>>("alternative-command-manager.ignored-commands", Collections.singletonList("warp test")) {
    @Override
    public Object getFromConfig(Config config) {
      return config.get(getPath());
    }

    @Override
    public List<String> convert(Object rawValue) {
      return CollectionUtils.createStringListFromObject(rawValue, true);
    }

    @Override
    public Object convertToRaw(List<String> value) {
      return value;
    }
  };

  public MainConfig(Plugin plugin) {
    super(new BukkitConfig(plugin, "config.yml"));
  }
}
