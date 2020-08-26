package me.hsgamer.bettergui.config;

import java.util.Collections;
import java.util.List;
import me.hsgamer.hscore.bukkit.config.ConfigPath;
import me.hsgamer.hscore.bukkit.config.PluginConfig;
import me.hsgamer.hscore.bukkit.config.path.BooleanConfigPath;
import me.hsgamer.hscore.bukkit.config.path.StringConfigPath;
import me.hsgamer.hscore.common.CommonUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainConfig extends PluginConfig {

  public static final StringConfigPath DEFAULT_MENU_TYPE = new StringConfigPath("default-menu-type",
      "simple");
  public static final StringConfigPath DEFAULT_ICON_TYPE = new StringConfigPath("default-icon-type",
      "simple");
  public static final BooleanConfigPath METRICS = new BooleanConfigPath("metrics", true);
  public static final BooleanConfigPath MODERN_CLICK_TYPE = new BooleanConfigPath(
      "use-modern-click-type", false);
  public static final BooleanConfigPath REPLACE_ALL_VARIABLES = new BooleanConfigPath(
      "replace-all-variables-each-check", true);
  public static final BooleanConfigPath FORCED_UPDATE_INVENTORY = new BooleanConfigPath(
      "forced-update-inventory", false);
  public static final BooleanConfigPath ENABLE_ALTERNATIVE_COMMAND_MANAGER = new BooleanConfigPath(
      "alternative-command-manager.enable", false);
  public static final BooleanConfigPath ALTERNATIVE_COMMAND_MANAGER_CASE_INSENSITIVE = new BooleanConfigPath(
      "alternative-command-manager.case-insensitive", true);
  public static final ConfigPath<List<String>> ALTERNATIVE_COMMAND_MANAGER_IGNORED_COMMANDS = new ConfigPath<>(
      "alternative-command-manager.ignored-commands",
      Collections.singletonList("warp test"),
      o -> CommonUtils.createStringListFromObject(o, true));

  public MainConfig(JavaPlugin plugin) {
    super(plugin, "config.yml");
    getConfig().options().copyDefaults(true);
    setDefaultPath();
    saveConfig();
  }

  private void setDefaultPath() {
    DEFAULT_ICON_TYPE.setConfig(this);
    DEFAULT_MENU_TYPE.setConfig(this);
    METRICS.setConfig(this);
    MODERN_CLICK_TYPE.setConfig(this);
    REPLACE_ALL_VARIABLES.setConfig(this);
    FORCED_UPDATE_INVENTORY.setConfig(this);
    ENABLE_ALTERNATIVE_COMMAND_MANAGER.setConfig(this);
    ALTERNATIVE_COMMAND_MANAGER_CASE_INSENSITIVE.setConfig(this);
    ALTERNATIVE_COMMAND_MANAGER_IGNORED_COMMANDS.setConfig(this);
  }
}
