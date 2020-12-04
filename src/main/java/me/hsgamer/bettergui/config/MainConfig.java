package me.hsgamer.bettergui.config;

import me.hsgamer.hscore.bukkit.config.PluginConfig;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.BaseConfigPath;
import me.hsgamer.hscore.config.PathLoader;
import me.hsgamer.hscore.config.path.BooleanConfigPath;
import me.hsgamer.hscore.config.path.StringConfigPath;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public final class MainConfig extends PluginConfig {

  public static final StringConfigPath DEFAULT_MENU_TYPE = new StringConfigPath("default-menu-type", "simple");
  public static final StringConfigPath DEFAULT_ICON_TYPE = new StringConfigPath("default-icon-type", "simple");
  public static final BooleanConfigPath METRICS = new BooleanConfigPath("metrics", true);
  public static final BooleanConfigPath MODERN_CLICK_TYPE = new BooleanConfigPath("use-modern-click-type", false);
  public static final BooleanConfigPath REPLACE_ALL_VARIABLES = new BooleanConfigPath("replace-all-variables-each-check", true);
  public static final BooleanConfigPath FORCED_UPDATE_INVENTORY = new BooleanConfigPath("forced-update-inventory", false);
  public static final BooleanConfigPath ENABLE_ALTERNATIVE_COMMAND_MANAGER = new BooleanConfigPath("alternative-command-manager.enable", false);
  public static final BooleanConfigPath ALTERNATIVE_COMMAND_MANAGER_CASE_INSENSITIVE = new BooleanConfigPath("alternative-command-manager.case-insensitive", true);
  public static final BaseConfigPath<List<String>> ALTERNATIVE_COMMAND_MANAGER_IGNORED_COMMANDS = new BaseConfigPath<>("alternative-command-manager.ignored-commands", Collections.singletonList("warp test"), o -> CollectionUtils.createStringListFromObject(o, true));

  public MainConfig(JavaPlugin plugin) {
    super(plugin, "config.yml");
    getConfig().options().copyDefaults(true);
    PathLoader.loadPath(this);
    saveConfig();
  }
}
