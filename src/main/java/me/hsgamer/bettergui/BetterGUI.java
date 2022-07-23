package me.hsgamer.bettergui;

import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.manager.MenuCommandManager;
import me.hsgamer.bettergui.manager.MenuManager;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;

/**
 * The main class of the plugin
 */
public final class BetterGUI extends BasePlugin {
  private static BetterGUI instance;
  private final MainConfig mainConfig = new MainConfig(this);
  private final MenuManager menuManager = new MenuManager(this);
  private final MenuCommandManager menuCommandManager = new MenuCommandManager();

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

  @Override
  public void enable() {
    Permissions.register();
  }

  @Override
  public void postEnable() {
    menuManager.loadMenuConfig();
  }

  @Override
  public void disable() {
    menuCommandManager.clearMenuCommand();
    menuManager.clear();
  }

  @Override
  public void postDisable() {
    Permissions.unregister();
  }

  /**
   * Get the main config
   *
   * @return the main config
   */
  public MainConfig getMainConfig() {
    return mainConfig;
  }

  /**
   * Get the menu manager
   *
   * @return the menu manager
   */
  public MenuManager getMenuManager() {
    return menuManager;
  }

  /**
   * Get the menu command manager
   *
   * @return the menu command manager
   */
  public MenuCommandManager getMenuCommandManager() {
    return menuCommandManager;
  }
}
