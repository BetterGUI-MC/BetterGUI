package me.hsgamer.bettergui;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import fr.mrmicky.fastinv.FastInvManager;
import me.hsgamer.bettergui.builder.CommandBuilder;
import me.hsgamer.bettergui.builder.IconBuilder;
import me.hsgamer.bettergui.builder.PropertyBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.command.ItemCommand;
import me.hsgamer.bettergui.config.impl.ItemConfig;
import me.hsgamer.bettergui.hook.PlaceholderAPIHook;
import me.hsgamer.bettergui.manager.AddonManager;
import me.hsgamer.bettergui.manager.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterGUI extends JavaPlugin {

  private static BetterGUI instance;

  private static TaskChainFactory taskChainFactory;
  private AddonManager addonManager = new AddonManager();
  private CommandManager commandManager = new CommandManager();

  private ItemConfig itemConfig;

  public static <T> TaskChain<T> newChain() {
    return taskChainFactory.newChain();
  }

  public static <T> TaskChain<T> newSharedChain(String name) {
    return taskChainFactory.newSharedChain(name);
  }

  public static BetterGUI getInstance() {
    return instance;
  }

  public AddonManager getAddonManager() {
    return addonManager;
  }

  @Override
  public void onEnable() {
    instance = this;
    FastInvManager.register(this);
    taskChainFactory = BukkitTaskChainFactory.create(this);

    if (PlaceholderAPIHook.setupPlugin()) {
      getLogger().info("Hooked PlaceholderAPI");
    }

    addonManager.loadAddons();

    Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
      addonManager.enableAddons();
      loadCommands();
      load();
    });
  }

  public void load() {
    // Check the classes
    CommandBuilder.checkClass();
    RequirementBuilder.checkClass();
    PropertyBuilder.checkClass();
    IconBuilder.checkClass();

    // Initialize the files
    itemConfig = new ItemConfig(this);
  }

  public void loadCommands() {
    commandManager.register(new ItemCommand());
  }

  @Override
  public void onDisable() {
    addonManager.disableAddons();
  }

  public ItemConfig getItemsConfig() {
    return itemConfig;
  }

  public CommandManager getCommandManager() {
    return commandManager;
  }
}
