package me.hsgamer.bettergui;

import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;

/**
 * The main class of the plugin
 */
public final class BetterGUI extends BasePlugin {
  private static BetterGUI instance;
  private final MainConfig mainConfig = new MainConfig(this);

  /**
   * Get the instance of the plugin
   *
   * @return the instance
   */
  public static BetterGUI getInstance() {
    return instance;
  }

  @Override
  public void preLoad() {
    instance = this;
  }

  @Override
  public void load() {
    mainConfig.setup();
  }

  /**
   * Get the main config
   *
   * @return the main config
   */
  public MainConfig getMainConfig() {
    return mainConfig;
  }
}
