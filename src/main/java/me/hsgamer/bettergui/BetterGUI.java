package me.hsgamer.bettergui;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.command.*;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.config.TemplateButtonConfig;
import me.hsgamer.bettergui.downloader.AddonDownloader;
import me.hsgamer.bettergui.hook.BetterGUIPlaceholderExpansion;
import me.hsgamer.bettergui.hook.PlaceholderAPIHook;
import me.hsgamer.bettergui.listener.AlternativeCommandListener;
import me.hsgamer.bettergui.manager.BetterGUIAddonManager;
import me.hsgamer.bettergui.manager.MenuCommandManager;
import me.hsgamer.bettergui.manager.MenuManager;
import me.hsgamer.bettergui.manager.PluginVariableManager;
import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.command.CommandManager;
import me.hsgamer.hscore.bukkit.config.PluginConfig;
import me.hsgamer.hscore.bukkit.gui.GUIListener;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.checker.spigotmc.SimpleVersionChecker;
import me.hsgamer.hscore.variable.ExternalStringReplacer;
import me.hsgamer.hscore.variable.VariableManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.*;

public final class BetterGUI extends BasePlugin {

  private static BetterGUI instance;
  private static TaskChainFactory taskChainFactory;

  private final MainConfig mainConfig = new MainConfig(this);
  private final MessageConfig messageConfig = new MessageConfig(this);
  private final TemplateButtonConfig templateButtonConfig = new TemplateButtonConfig(this);

  private final MenuManager menuManager = new MenuManager(this);
  private final MenuCommandManager menuCommandManager = new MenuCommandManager();
  private final BetterGUIAddonManager addonManager = new BetterGUIAddonManager(this);
  private final AddonDownloader addonDownloader = new AddonDownloader(this);

  /**
   * Create a new task chain
   *
   * @param <T> the type of the input
   *
   * @return the task chain
   */
  public static <T> TaskChain<T> newChain() {
    return taskChainFactory.newChain();
  }

  /**
   * Create a new shared task chain
   *
   * @param <T>  the type of the input
   * @param name the name of the task chain
   *
   * @return the task chain
   */
  public static <T> TaskChain<T> newSharedChain(String name) {
    return taskChainFactory.newSharedChain(name);
  }

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
    MessageUtils.setPrefix(MessageConfig.PREFIX::getValue);

    if (getDescription().getVersion().contains("SNAPSHOT")) {
      getLogger().warning("You are using the development version");
      getLogger().warning("This is not ready for production");
      getLogger().warning("Use in your own risk");
    } else {
      new SimpleVersionChecker(75620).getVersion().thenAccept(output -> {
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
    VariableManager.setReplaceAll(MainConfig.REPLACE_ALL_VARIABLES::getValue);
    PluginVariableManager.registerDefaultVariables();
  }

  @Override
  public void enable() {
    GUIListener.init(this);
    taskChainFactory = BukkitTaskChainFactory.create(this);

    // Check PlaceholderAPI hook
    if (PlaceholderAPIHook.setupPlugin()) {
      VariableManager.addExternalReplacer(new ExternalStringReplacer() {
        @Override
        public String replace(String original, UUID uuid) {
          return PlaceholderAPIHook.setPlaceholders(original, Bukkit.getOfflinePlayer(uuid));
        }

        @Override
        public boolean canBeReplaced(String original) {
          return PlaceholderAPIHook.hasPlaceholders(original);
        }
      });
      new BetterGUIPlaceholderExpansion().register();
    }

    // Check Alternative Command Manager
    if (Boolean.TRUE.equals(MainConfig.ENABLE_ALTERNATIVE_COMMAND_MANAGER.getValue())) {
      getLogger().info("Enabled alternative command manager");
      getServer().getPluginManager().registerEvents(new AlternativeCommandListener(), this);
    }

    // Load addons
    addonManager.loadAddons();

    // Register default command
    registerCommand(new OpenCommand());
    registerCommand(new MainCommand(getName().toLowerCase()));
    registerCommand(new GetAddonsCommand());
    registerCommand(new ReloadCommand());
    registerCommand(new GetVariablesCommand());
    registerCommand(new AddonDownloaderCommand());
  }

  @Override
  public void postEnable() {
    addonManager.enableAddons();
    loadMenuConfig();
    addonManager.callPostEnable();
    CommandManager.syncCommand();
    addonDownloader.createMenu();

    if (Boolean.TRUE.equals(MainConfig.METRICS.getValue())) {
      Metrics metrics = new Metrics(this, 6609);
      metrics.addCustomChart(new Metrics.DrilldownPie("addon", () -> {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        Map<String, Integer> addons = addonManager.getAddonCount();
        map.put(String.valueOf(addons.size()), addons);
        return map;
      }));
      metrics.addCustomChart(new Metrics.AdvancedPie("addon_count", addonManager::getAddonCount));
    }
  }

  /**
   * Load the menu config
   */
  public void loadMenuConfig() {
    File menusFolder = new File(getDataFolder(), "menu");
    if (!menusFolder.exists() && menusFolder.mkdirs()) {
      saveResource("menu" + File.separator + "example.yml", false);
    }
    for (PluginConfig pluginConfig : getMenuConfig(menusFolder)) {
      menuManager.registerMenu(pluginConfig);
    }
  }

  /**
   * Get the menu config
   *
   * @param file the folder
   *
   * @return the menu config
   */
  private List<PluginConfig> getMenuConfig(File file) {
    List<PluginConfig> list = new ArrayList<>();
    if (file.isDirectory()) {
      for (File subFile : Objects.requireNonNull(file.listFiles())) {
        list.addAll(getMenuConfig(subFile));
      }
    } else if (file.isFile() && file.getName().endsWith(".yml")) {
      list.add(new PluginConfig(file));
    }
    return list;
  }

  @Override
  public void disable() {
    menuCommandManager.clearMenuCommand();
    menuManager.clear();
    addonManager.disableAddons();
    addonDownloader.stopMenu();
    RequirementBuilder.INSTANCE.unregisterAll();
    ButtonBuilder.INSTANCE.unregisterAll();
    ActionBuilder.INSTANCE.unregisterAll();
    MenuBuilder.INSTANCE.unregisterAll();
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
  public BetterGUIAddonManager getAddonManager() {
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
   * Get the template button config
   *
   * @return the template button config
   */
  public TemplateButtonConfig getTemplateButtonConfig() {
    return templateButtonConfig;
  }
}
