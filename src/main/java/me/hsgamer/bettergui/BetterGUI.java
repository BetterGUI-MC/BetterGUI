package me.hsgamer.bettergui;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import fr.mrmicky.fastinv.FastInvManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import me.hsgamer.bettergui.builder.CommandBuilder;
import me.hsgamer.bettergui.builder.IconBuilder;
import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.bettergui.builder.PropertyBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.command.GetAddonsCommand;
import me.hsgamer.bettergui.command.MainCommand;
import me.hsgamer.bettergui.command.OpenCommand;
import me.hsgamer.bettergui.command.ReloadCommand;
import me.hsgamer.bettergui.config.PluginConfig;
import me.hsgamer.bettergui.config.impl.MainConfig;
import me.hsgamer.bettergui.config.impl.MainConfig.DefaultConfig;
import me.hsgamer.bettergui.config.impl.MessageConfig;
import me.hsgamer.bettergui.hook.PlaceholderAPIHook;
import me.hsgamer.bettergui.manager.AddonManager;
import me.hsgamer.bettergui.manager.CommandManager;
import me.hsgamer.bettergui.manager.MenuManager;
import me.hsgamer.bettergui.util.VersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterGUI extends JavaPlugin {

  private static BetterGUI instance;

  private static TaskChainFactory taskChainFactory;
  private final AddonManager addonManager = new AddonManager(this);
  private final CommandManager commandManager = new CommandManager(this);
  private final MenuManager menuManager = new MenuManager();

  private final MainConfig mainConfig = new MainConfig(this);
  private final MessageConfig messageConfig = new MessageConfig(this);

  public static <T> TaskChain<T> newChain() {
    return taskChainFactory.newChain();
  }

  @SuppressWarnings("unused")
  public static TaskChainFactory getTaskChainFactory() {
    return taskChainFactory;
  }

  public static BetterGUI getInstance() {
    return instance;
  }

  public AddonManager getAddonManager() {
    return addonManager;
  }

  @Override
  public void onLoad() {
    instance = this;
  }

  @Override
  public void onEnable() {
    FastInvManager.register(this);
    taskChainFactory = BukkitTaskChainFactory.create(this);

    getLogger().info("");
    getLogger().info("    ____       __  __               ________  ______");
    getLogger().info("   / __ )___  / /_/ /____  _____   / ____/ / / /  _/");
    getLogger().info("  / __  / _ \\/ __/ __/ _ \\/ ___/  / / __/ / / // /  ");
    getLogger().info(" / /_/ /  __/ /_/ /_/  __/ /     / /_/ / /_/ _/ /   ");
    getLogger().info("/_____/\\___/\\__/\\__/\\___/_/      \\____/\\____/___/");
    getLogger().info("");

    getLogger().log(Level.INFO, "\t\tVersion: {0}", getDescription().getVersion());
    if (getDescription().getVersion().contains("SNAPSHOT")) {
      getLogger().warning("You are using the development version");
      getLogger().warning("This is not ready for production");
      getLogger().warning("Use in your own risk");
    } else {
      new VersionChecker(this, 75620).getVersion(version -> {
        if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
          getLogger().info("You are using the latest version");
        } else {
          getLogger().warning("There is an available update");
          getLogger().warning("New Version: " + version);
        }
      });
    }

    if (PlaceholderAPIHook.setupPlugin()) {
      getLogger().info("Hooked PlaceholderAPI");
    }

    addonManager.loadAddons();

    Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
      checkClass();
      loadCommands();
      addonManager.enableAddons();
      addonManager.callPostEnable();
      loadMenuConfig();
      commandManager.syncCommand();
      if (mainConfig.get(DefaultConfig.METRICS)) {
        enableMetrics();
      }
    });
  }

  public void checkClass() {
    CommandBuilder.checkClass();
    RequirementBuilder.checkClass();
    PropertyBuilder.checkClass();
    IconBuilder.checkClass();
    MenuBuilder.checkClass();
  }

  public void loadCommands() {
    commandManager.register(new OpenCommand());
    commandManager.register(new ReloadCommand());
    commandManager.register(new GetAddonsCommand());
    commandManager.register(new MainCommand(getName().toLowerCase()));
  }

  public void loadMenuConfig() {
    File menusFolder = new File(getDataFolder(), "menu");
    if (!menusFolder.exists()) {
      menusFolder.mkdirs();
      saveResource("menu" + File.separator + "example.yml", false);
    }
    for (PluginConfig pluginConfig : getMenuConfig(menusFolder)) {
      menuManager.registerMenu(pluginConfig);
    }
  }

  private List<PluginConfig> getMenuConfig(File file) {
    List<PluginConfig> list = new ArrayList<>();
    if (file.isDirectory()) {
      for (File subFile : Objects.requireNonNull(file.listFiles())) {
        list.addAll(getMenuConfig(subFile));
      }
    } else if (file.isFile() && file.getName().endsWith(".yml")) {
      list.add(new PluginConfig(this, file));
    }
    return list;
  }

  private void enableMetrics() {
    Metrics metrics = new Metrics(this, 6609);
    metrics.addCustomChart(new Metrics.DrilldownPie("addon", () -> {
      Map<String, Map<String, Integer>> map = new HashMap<>();
      Map<String, Integer> addons = addonManager.getAddonCount();
      map.put(String.valueOf(addons.containsKey("Empty") ? 0 : addons.size()), addons);
      return map;
    }));
    metrics.addCustomChart(new Metrics.AdvancedPie("addon_count", addonManager::getAddonCount));
  }

  @Override
  public void onDisable() {
    commandManager.clearMenuCommand();
    menuManager.clear();
    addonManager.disableAddons();
  }

  public CommandManager getCommandManager() {
    return commandManager;
  }

  public MenuManager getMenuManager() {
    return menuManager;
  }

  public MainConfig getMainConfig() {
    return mainConfig;
  }

  public MessageConfig getMessageConfig() {
    return messageConfig;
  }
}
