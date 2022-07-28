package me.hsgamer.bettergui;

import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.bettergui.command.*;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.config.TemplateButtonConfig;
import me.hsgamer.bettergui.downloader.AddonDownloader;
import me.hsgamer.bettergui.listener.AlternativeCommandListener;
import me.hsgamer.bettergui.manager.ExtraAddonManager;
import me.hsgamer.bettergui.manager.MenuCommandManager;
import me.hsgamer.bettergui.manager.MenuManager;
import me.hsgamer.bettergui.manager.PluginVariableManager;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.gui.GUIListener;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.checker.spigotmc.SpigotVersionChecker;
import me.hsgamer.hscore.task.BatchRunnable;
import me.hsgamer.hscore.variable.VariableManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.DrilldownPie;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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
  private final ExtraAddonManager addonManager = new ExtraAddonManager(this);
  private final AddonDownloader addonDownloader = new AddonDownloader(this);

  /**
   * Get the instance of the plugin
   *
   * @return the instance
   */
  public static BetterGUI getInstance() {
    return instance;
  }

  /**
   * Run the batch runnable
   *
   * @param runnable the runnable
   */
  public static void runBatchRunnable(BatchRunnable runnable) {
    Bukkit.getScheduler().runTaskAsynchronously(getInstance(), runnable);
  }

  /**
   * Run the batch runnable
   *
   * @param batchRunnableConsumer the batch runnable consumer
   */
  public static void runBatchRunnable(Consumer<BatchRunnable> batchRunnableConsumer) {
    BatchRunnable runnable = new BatchRunnable();
    batchRunnableConsumer.accept(runnable);
    runBatchRunnable(runnable);
  }

  @Override
  public void preLoad() {
    instance = this;
    MessageUtils.setPrefix(() -> messageConfig.prefix);

    if (getDescription().getVersion().contains("SNAPSHOT")) {
      getLogger().warning("You are using the development version");
      getLogger().warning("This is not ready for production");
      getLogger().warning("Use in your own risk");
    } else {
      new SpigotVersionChecker(75620).getVersion().thenAccept(output -> {
        if (output.startsWith("Error when getting version:")) {
          getLogger().warning(output);
        } else if (this.getDescription().getVersion().equalsIgnoreCase(output)) {
          getLogger().info("You are using the latest version");
        } else {
          getLogger().warning("There is an available update");
          getLogger().warning("New Version: " + output);
        }
      });
    }
  }

  @Override
  public void load() {
    VariableManager.setReplaceAll(() -> mainConfig.replaceAllVariables);
    PluginVariableManager.registerDefaultVariables();
    mainConfig.setup();
    messageConfig.setup();
    templateButtonConfig.setup();
    addonDownloader.setup();
  }

  @Override
  public void enable() {
    Permissions.register();

    GUIListener.init(this);

    if (mainConfig.alternativeCommandManager.enable) {
      getLogger().info("Enabled alternative command manager");
      registerListener(new AlternativeCommandListener(this));
    }

    addonManager.loadAddons();

    registerCommand(new OpenCommand(this));
    registerCommand(new MainCommand(this));
    registerCommand(new GetAddonsCommand(this));
    registerCommand(new ReloadCommand(this));
    registerCommand(new GetVariablesCommand());
    registerCommand(new GetTemplateButtonsCommand(this));
  }

  @Override
  public void postEnable() {
    addonManager.enableAddons();
    menuManager.loadMenuConfig();
    addonManager.callPostEnable();

    if (mainConfig.metrics) {
      Metrics metrics = new Metrics(this, 6609);
      metrics.addCustomChart(new DrilldownPie("addon", () -> {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        Map<String, Integer> addons = addonManager.getAddonCount();
        map.put(String.valueOf(addons.size()), addons);
        return map;
      }));
      metrics.addCustomChart(new AdvancedPie("addon_count", addonManager::getAddonCount));
    }
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

  /**
   * Get the addon manager
   *
   * @return the addon manager
   */
  public ExtraAddonManager getAddonManager() {
    return addonManager;
  }

  /**
   * Get the addon downloader
   *
   * @return the addon downloader
   */
  public AddonDownloader getAddonDownloader() {
    return addonDownloader;
  }
}
