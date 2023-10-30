package me.hsgamer.bettergui;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.*;
import me.hsgamer.bettergui.command.*;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.config.TemplateConfig;
import me.hsgamer.bettergui.downloader.AddonDownloader;
import me.hsgamer.bettergui.manager.AddonManager;
import me.hsgamer.bettergui.manager.MenuCommandManager;
import me.hsgamer.bettergui.manager.MenuManager;
import me.hsgamer.bettergui.papi.ExtraPlaceholderExpansion;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIListener;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.bukkit.variable.BukkitVariableBundle;
import me.hsgamer.hscore.checker.spigotmc.SpigotVersionChecker;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.hscore.task.BatchRunnable;
import me.hsgamer.hscore.variable.VariableBundle;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.DrilldownPie;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The main class of the plugin
 */
public final class BetterGUI extends BasePlugin {
  private static BetterGUI instance;
  private final MainConfig mainConfig = ConfigGenerator.newInstance(MainConfig.class, new BukkitConfig(this, "config.yml"));
  private final MessageConfig messageConfig = ConfigGenerator.newInstance(MessageConfig.class, new BukkitConfig(this, "messages.yml"));
  private final TemplateConfig templateButtonConfig = new TemplateConfig(this);
  private final MenuManager menuManager = new MenuManager(this);
  private final MenuCommandManager menuCommandManager = new MenuCommandManager(this);
  private final AddonManager addonManager = new AddonManager(this);
  private final AddonDownloader addonDownloader = new AddonDownloader(this);
  private final VariableBundle variableBundle = new VariableBundle();

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
    Scheduler.current().async().runTask(runnable);
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
  }

  @Override
  public void load() {
    MessageUtils.setPrefix(messageConfig::getPrefix);

    BukkitVariableBundle.registerVariables(variableBundle);
    variableBundle.register("menu_", StringReplacer.of((original, uuid) -> {
      String[] split = original.split("_", 2);
      String menuName = split[0].trim();
      String variable = split.length > 1 ? split[1].trim() : "";
      Menu menu = menuManager.getMenu(menuName);
      if (menu == null) {
        return null;
      }
      return menu.getVariableManager().setVariables(StringReplacerApplier.normalizeQuery(variable), uuid);
    }));
  }

  @Override
  public void enable() {
    BukkitGUIListener.init(this);

    addonManager.loadExpansions();

    registerCommand(new OpenCommand(this));
    registerCommand(new MainCommand(this));
    registerCommand(new GetAddonsCommand(this));
    registerCommand(new ReloadCommand(this));
    registerCommand(new GetVariablesCommand(this));
    registerCommand(new GetTemplateButtonsCommand(this));

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      ExtraPlaceholderExpansion expansion = new ExtraPlaceholderExpansion(this);
      expansion.register();
      addDisableFunction(expansion::unregister);
    }
  }

  @Override
  public void postEnable() {
    addonManager.enableExpansions();
    addonDownloader.setup();
    templateButtonConfig.setIncludeMenuInTemplate(mainConfig.isIncludeMenuInTemplate());
    templateButtonConfig.setup();
    menuManager.loadMenuConfig();

    Metrics metrics = new Metrics(this, 6609);
    metrics.addCustomChart(new DrilldownPie("addon", () -> {
      Map<String, Map<String, Integer>> map = new HashMap<>();
      Map<String, Integer> addons = addonManager.getExpansionCount();
      map.put(String.valueOf(addons.size()), addons);
      return map;
    }));
    metrics.addCustomChart(new AdvancedPie("addon_count", addonManager::getExpansionCount));

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
  public void disable() {
    menuCommandManager.clearMenuCommand();
    menuManager.clear();
    templateButtonConfig.clear();
    addonManager.disableExpansions();
    addonManager.clearExpansions();
  }

  @Override
  public void postDisable() {
    ActionBuilder.INSTANCE.clear();
    ButtonBuilder.INSTANCE.clear();
    ItemModifierBuilder.INSTANCE.clear();
    MenuBuilder.INSTANCE.clear();
    RequirementBuilder.INSTANCE.clear();
    variableBundle.unregisterAll();
  }

  @Override
  protected List<Class<?>> getPermissionClasses() {
    return Collections.singletonList(Permissions.class);
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
  public TemplateConfig getTemplateButtonConfig() {
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
  public AddonManager getAddonManager() {
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

  /**
   * Get the variable bundle
   *
   * @return the variable bundle
   */
  public VariableBundle getVariableBundle() {
    return variableBundle;
  }
}
