package me.hsgamer.bettergui;

import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.bettergui.command.OpenCommand;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.config.TemplateButtonConfig;
import me.hsgamer.bettergui.listener.AlternativeCommandListener;
import me.hsgamer.bettergui.manager.MenuCommandManager;
import me.hsgamer.bettergui.manager.MenuManager;
import me.hsgamer.bettergui.manager.PluginVariableManager;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.gui.GUIListener;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.variable.VariableManager;

/**
 * The main class of the plugin
 */
public final class BetterGUI extends BasePlugin {
  private static BetterGUI instance;
  private final MainConfig mainConfig = new MainConfig(this);
  private final MessageConfig messageConfig = new MessageConfig(this);
  private final TemplateButtonConfig templateButtonConfig = new TemplateButtonConfig(this);
  private final MenuManager menuManager = new MenuManager(this);
  private final MenuCommandManager menuCommandManager = new MenuCommandManager(this);

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
    MessageUtils.setPrefix(() -> messageConfig.prefix);
  }

  @Override
  public void load() {
    VariableManager.setReplaceAll(() -> mainConfig.replaceAllVariables);
    PluginVariableManager.registerDefaultVariables();
    mainConfig.setup();
    messageConfig.setup();
  }

  @Override
  public void enable() {
    Permissions.register();

    GUIListener.init(this);

    if (mainConfig.alternativeCommandManager.enable) {
      getLogger().info("Enabled alternative command manager");
      registerListener(new AlternativeCommandListener(this));
    }

    registerCommand(new OpenCommand(this));
  }

  @Override
  public void postEnable() {
    menuManager.loadMenuConfig();
  }

  @Override
  public void disable() {
    menuCommandManager.clearMenuCommand();
    menuManager.clear();
    templateButtonConfig.clear();
  }

  @Override
  public void postDisable() {
    Permissions.unregister();
    ButtonBuilder.INSTANCE.clear();
    MenuBuilder.INSTANCE.clear();
    PluginVariableManager.unregisterAll();
    VariableManager.clearExternalReplacers();
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
   * Get the message config
   *
   * @return the message config
   */
  public MessageConfig getMessageConfig() {
    return messageConfig;
  }

  /**
   * Get the template button config
   *
   * @return the template button config
   */
  public TemplateButtonConfig getTemplateButtonConfig() {
    return templateButtonConfig;
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
