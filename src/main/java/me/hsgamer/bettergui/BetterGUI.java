package me.hsgamer.bettergui;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.cryptomorin.xseries.XMaterial;
import fr.mrmicky.fastinv.FastInvManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import me.hsgamer.bettergui.command.AddonDownloaderCommand;
import me.hsgamer.bettergui.command.GetAddonsCommand;
import me.hsgamer.bettergui.command.MainCommand;
import me.hsgamer.bettergui.command.OpenCommand;
import me.hsgamer.bettergui.command.ReloadCommand;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.downloader.AddonDownloader;
import me.hsgamer.bettergui.hook.PlaceholderAPIHook;
import me.hsgamer.bettergui.manager.AddonManager;
import me.hsgamer.bettergui.manager.CommandManager;
import me.hsgamer.bettergui.manager.MenuManager;
import me.hsgamer.bettergui.manager.VariableManager;
import me.hsgamer.hscore.bukkit.config.PluginConfig;
import me.hsgamer.hscore.bukkit.updater.VersionChecker;
import me.hsgamer.hscore.bukkit.utils.BukkitUtils;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.expression.ExpressionUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterGUI extends JavaPlugin {

  private static BetterGUI instance;

  private static TaskChainFactory taskChainFactory;
  private final AddonManager addonManager = new AddonManager(this);
  private final CommandManager commandManager = new CommandManager(this);
  private final MenuManager menuManager = new MenuManager();
  private final AddonDownloader addonDownloader = new AddonDownloader(this);

  private final MainConfig mainConfig = new MainConfig(this);
  private final MessageConfig messageConfig = new MessageConfig(this);

  public static <T> TaskChain<T> newChain() {
    return taskChainFactory.newChain();
  }

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
    MessageUtils.setPrefix(MessageConfig.PREFIX::getValue);
  }

  @Override
  public void onEnable() {
    if (getDescription().getVersion().contains("SNAPSHOT")) {
      getLogger().warning("You are using the development version");
      getLogger().warning("This is not ready for production");
      getLogger().warning("Use in your own risk");
    } else {
      new VersionChecker(75620).getVersion().thenAccept(output -> {
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

    registerDefaultVariables();

    FastInvManager.register(this);
    taskChainFactory = BukkitTaskChainFactory.create(this);
    addonDownloader.createMenu();

    if (PlaceholderAPIHook.setupPlugin()) {
      getLogger().info("Hooked PlaceholderAPI");
    }

    addonManager.loadAddons();

    Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
      loadCommands();
      addonManager.enableAddons();
      loadMenuConfig();
      addonManager.callPostEnable();
      commandManager.syncCommand();
      if (MainConfig.METRICS.getValue().equals(Boolean.TRUE)) {
        enableMetrics();
      }
    });
  }

  private void loadCommands() {
    commandManager.register(new OpenCommand());
    commandManager.register(new ReloadCommand());
    commandManager.register(new GetAddonsCommand());
    commandManager.register(new MainCommand(getName().toLowerCase()));
    commandManager.register(new AddonDownloaderCommand());
  }

  private void registerDefaultVariables() {
    // Player Name
    VariableManager.register("player", (executor, identifier) -> executor.getName());

    // Online Player
    VariableManager.register("online",
        (executor, identifier) -> String.valueOf(Bukkit.getOnlinePlayers().size()));

    // Max Players
    VariableManager
        .register("max_players", (executor, identifier) -> String.valueOf(Bukkit.getMaxPlayers()));

    // Location
    VariableManager.register("world", (executor, identifier) -> {
      if (executor.isOnline()) {
        return executor.getPlayer().getWorld().getName();
      }
      return "";
    });
    VariableManager.register("x", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getLocation().getX());
      }
      return "";
    });
    VariableManager.register("y", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getLocation().getY());
      }
      return "";
    });
    VariableManager.register("z", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getLocation().getZ());
      }
      return "";
    });

    // Bed Location
    VariableManager.register("bed_", ((executor, identifier) -> {
      if (executor.getBedSpawnLocation() == null) {
        return null;
      } else if (identifier.equalsIgnoreCase("world")) {
        return executor.getBedSpawnLocation().getWorld().getName();
      } else if (identifier.equalsIgnoreCase("x")) {
        return String.valueOf(executor.getBedSpawnLocation().getX());
      } else if (identifier.equalsIgnoreCase("y")) {
        return String.valueOf(executor.getBedSpawnLocation().getY());
      } else if (identifier.equalsIgnoreCase("z")) {
        return String.valueOf(executor.getBedSpawnLocation().getZ());
      } else {
        return null;
      }
    }));

    // Exp
    VariableManager.register("exp", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getTotalExperience());
      }
      return "";
    });

    // Level
    VariableManager.register("level", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getLevel());
      }
      return "";
    });

    // Exp to level
    VariableManager.register("exp_to_level", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getExpToLevel());
      }
      return "";
    });

    // Food Level
    VariableManager.register("food_level", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getFoodLevel());
      }
      return "";
    });

    // IP
    VariableManager.register("ip", (executor, identifier) -> {
      if (executor.isOnline()) {
        return executor.getPlayer().getAddress().getAddress().getHostAddress();
      }
      return "";
    });

    // Biome
    VariableManager.register("biome", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getLocation().getBlock().getBiome());
      }
      return "";
    });

    // Ping
    VariableManager.register("ping", ((executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(BukkitUtils.getPing(executor.getPlayer()));
      }
      return "";
    }));

    // Rainbow Color
    VariableManager.register("rainbow", (executor, identifier) -> {
      ChatColor[] values = ChatColor.values();
      ChatColor color;
      do {
        color = values[ThreadLocalRandom.current().nextInt(values.length - 1)];
      } while (color.equals(ChatColor.BOLD)
          || color.equals(ChatColor.ITALIC)
          || color.equals(ChatColor.STRIKETHROUGH)
          || color.equals(ChatColor.RESET)
          || color.equals(ChatColor.MAGIC)
          || color.equals(ChatColor.UNDERLINE));
      return MessageUtils.colorize("&" + color.getChar());
    });

    // Random
    VariableManager.register("random_", (executor, identifier) -> {
      identifier = identifier.trim();
      if (identifier.contains(":")) {
        String[] split = identifier.split(":", 2);
        String s1 = split[0].trim();
        String s2 = split[1].trim();
        if (Validate.isValidInteger(s1) && Validate.isValidInteger(s2)) {
          int i1 = Integer.parseInt(s1);
          int i2 = Integer.parseInt(s2);
          int max = Math.max(i1, i2);
          int min = Math.min(i1, i2);
          return String.valueOf(ThreadLocalRandom.current().nextInt(min, max + 1));
        }
      } else if (Validate.isValidInteger(identifier)) {
        return String.valueOf(ThreadLocalRandom.current().nextInt(Integer.parseInt(identifier)));
      }
      return null;
    });

    // Condition
    VariableManager.register("condition_", (executor, identifier) -> {
      if (ExpressionUtils.isValidExpression(identifier)) {
        return ExpressionUtils.getResult(identifier).toString();
      }
      return null;
    });

    // UUID
    VariableManager.register("uuid", (executor, identifier) -> executor.getUniqueId().toString());

    // Hex Color
    if (XMaterial.supports(16)) {
      VariableManager.register("hcolor_", (executor, identifier) -> String
          .valueOf(net.md_5.bungee.api.ChatColor.of("#" + identifier)));
      VariableManager.register("hrainbow", (offlinePlayer, s) ->
          String.valueOf(net.md_5.bungee.api.ChatColor
              .of("#" + String.format("%06x", ThreadLocalRandom.current().nextInt(0xFFFFFF + 1)))));
    }
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
    addonDownloader.cancelTask();
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

  public AddonDownloader getAddonDownloader() {
    return addonDownloader;
  }
}
