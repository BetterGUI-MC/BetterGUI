package me.hsgamer.bettergui.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.BaseConfigPath;
import me.hsgamer.hscore.config.ConfigPath;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.BooleanConfigPath;
import me.hsgamer.hscore.config.path.StringConfigPath;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

/**
 * The main config of the plugin
 */
public final class MainConfig extends PathableConfig {

  public static final StringConfigPath DEFAULT_MENU_TYPE = new StringConfigPath("default-menu-type", "simple");
  public static final StringConfigPath DEFAULT_BUTTON_TYPE = new StringConfigPath("default-button-type", "simple");
  public static final BooleanConfigPath METRICS = new BooleanConfigPath("metrics", true);
  public static final BooleanConfigPath MODERN_CLICK_TYPE = new BooleanConfigPath("use-modern-click-type", false);
  public static final BooleanConfigPath REPLACE_ALL_VARIABLES = new BooleanConfigPath("replace-all-variables-each-check", true);
  public static final BooleanConfigPath FORCED_UPDATE_INVENTORY = new BooleanConfigPath("forced-update-inventory", false);
  public static final BooleanConfigPath ENABLE_ALTERNATIVE_COMMAND_MANAGER = new BooleanConfigPath("alternative-command-manager.enable", false);
  public static final BooleanConfigPath ALTERNATIVE_COMMAND_MANAGER_CASE_INSENSITIVE = new BooleanConfigPath("alternative-command-manager.case-insensitive", true);
  public static final BooleanConfigPath ALTERNATIVE_COMMAND_MANAGER_BLACKLIST = new BooleanConfigPath("alternative-command-manager.blacklist", true);
  public static final BooleanConfigPath USE_LEGACY_BUTTON = new BooleanConfigPath("use-legacy-button", true);
  public static final ConfigPath<List<String>> ALTERNATIVE_COMMAND_MANAGER_IGNORED_COMMANDS = new BaseConfigPath<>("alternative-command-manager.ignored-commands", Collections.singletonList("warp test"), o -> CollectionUtils.createStringListFromObject(o, true));

  public MainConfig(Plugin plugin) {
    super(new BukkitConfig(plugin, "config.yml"));
  }
}
