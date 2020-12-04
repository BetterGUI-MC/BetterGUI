package me.hsgamer.bettergui;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.hscore.bukkit.utils.BukkitUtils;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.checker.spigotmc.SimpleVersionChecker;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.expression.ExpressionUtils;
import me.hsgamer.hscore.variable.VariableManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class BetterGUI extends JavaPlugin {

  private static BetterGUI instance;
  private static TaskChainFactory taskChainFactory;

  private final MainConfig mainConfig = new MainConfig(this);
  private final MessageConfig messageConfig = new MessageConfig(this);

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
  public void onLoad() {
    instance = this;
    MessageUtils.setPrefix(MessageConfig.PREFIX::getValue);
    VariableManager.setReplaceAll(MainConfig.REPLACE_ALL_VARIABLES::getValue);

    registerDefaultVariables();

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
  public void onEnable() {
    taskChainFactory = BukkitTaskChainFactory.create(this);

    if (Boolean.TRUE.equals(MainConfig.ENABLE_ALTERNATIVE_COMMAND_MANAGER.getValue())) {
      getLogger().info("Enabled alternative command manager");
//      getServer().getPluginManager().registerEvents(new CommandListener(), this);
    }
  }

  /**
   * Register default variables
   */
  private void registerDefaultVariables() {
    // Player Name
    VariableManager.register("player", (original, uuid) -> Bukkit.getOfflinePlayer(uuid).getName());

    // Online Player
    VariableManager.register("online", (original, uuid) -> String.valueOf(Bukkit.getOnlinePlayers().size()));

    // Max Players
    VariableManager
      .register("max_players", (original, uuid) -> String.valueOf(Bukkit.getMaxPlayers()));

    // Location
    VariableManager.register("world", (original, uuid) -> Optional.ofNullable(Bukkit.getPlayer(uuid)).map(player -> player.getLocation().getWorld().getName()).orElse(""));
    VariableManager.register("x", (original, uuid) -> Optional.ofNullable(Bukkit.getPlayer(uuid)).map(Player::getLocation).map(Location::getX).map(String::valueOf).orElse(""));
    VariableManager.register("y", (original, uuid) -> Optional.ofNullable(Bukkit.getPlayer(uuid)).map(Player::getLocation).map(Location::getY).map(String::valueOf).orElse(""));
    VariableManager.register("z", (original, uuid) -> Optional.ofNullable(Bukkit.getPlayer(uuid)).map(Player::getLocation).map(Location::getZ).map(String::valueOf).orElse(""));

    // Bed Location
    VariableManager.register("bed_", (original, uuid) -> {
      Player player = Bukkit.getPlayer(uuid);
      if (player == null || player.getBedSpawnLocation() == null) {
        return "";
      } else if (original.equalsIgnoreCase("world")) {
        return player.getBedSpawnLocation().getWorld().getName();
      } else if (original.equalsIgnoreCase("x")) {
        return String.valueOf(player.getBedSpawnLocation().getX());
      } else if (original.equalsIgnoreCase("y")) {
        return String.valueOf(player.getBedSpawnLocation().getY());
      } else if (original.equalsIgnoreCase("z")) {
        return String.valueOf(player.getBedSpawnLocation().getZ());
      } else {
        return null;
      }
    });

    // Exp
    VariableManager.register("exp", (original, uuid) -> Optional.ofNullable(Bukkit.getPlayer(uuid)).map(Player::getTotalExperience).map(String::valueOf).orElse(""));

    // Level
    VariableManager.register("level", (original, uuid) -> Optional.ofNullable(Bukkit.getPlayer(uuid)).map(Player::getLevel).map(String::valueOf).orElse(""));

    // Exp to level
    VariableManager.register("exp_to_level", (original, uuid) -> Optional.ofNullable(Bukkit.getPlayer(uuid)).map(Player::getExpToLevel).map(String::valueOf).orElse(""));

    // Food Level
    VariableManager.register("food_level", (original, uuid) -> Optional.ofNullable(Bukkit.getPlayer(uuid)).map(Player::getFoodLevel).map(String::valueOf).orElse(""));

    // IP
    VariableManager.register("ip", (original, uuid) -> Optional.ofNullable(Bukkit.getPlayer(uuid)).map(player -> player.getAddress().getHostName()).orElse(""));

    // Biome
    VariableManager.register("biome", (original, uuid) -> Optional.ofNullable(Bukkit.getPlayer(uuid)).map(player -> player.getLocation().getBlock().getBiome().name()).orElse(""));

    // Ping
    VariableManager.register("ping", (original, uuid) -> Optional.ofNullable(Bukkit.getPlayer(uuid)).map(BukkitUtils::getPing).map(String::valueOf).orElse(""));

    // Rainbow Color
    VariableManager.register("rainbow", (original, uuid) -> {
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
    VariableManager.register("random_", (original, uuid) -> {
      original = original.trim();
      if (original.contains(":")) {
        String[] split = original.split(":", 2);
        String s1 = split[0].trim();
        String s2 = split[1].trim();
        if (Validate.isValidInteger(s1) && Validate.isValidInteger(s2)) {
          int i1 = Integer.parseInt(s1);
          int i2 = Integer.parseInt(s2);
          int max = Math.max(i1, i2);
          int min = Math.min(i1, i2);
          return String.valueOf(ThreadLocalRandom.current().nextInt(min, max + 1));
        }
      } else if (Validate.isValidInteger(original)) {
        return String.valueOf(ThreadLocalRandom.current().nextInt(Integer.parseInt(original)));
      }
      return null;
    });

    // Condition
    VariableManager.register("condition_", (original, uuid) -> Optional.ofNullable(ExpressionUtils.getResult(original)).map(BigDecimal::toString).orElse(null));

    // UUID
    VariableManager.register("uuid", (original, uuid) -> uuid.toString());
  }

  /**
   * Enable metrics (BStats)
   */
  private void enableMetrics() {
    Metrics metrics = new Metrics(this, 6609);
//    metrics.addCustomChart(new Metrics.DrilldownPie("addon", () -> {
//      Map<String, Map<String, Integer>> map = new HashMap<>();
//      Map<String, Integer> addons = addonManager.getAddonCount();
//      map.put(String.valueOf(addons.containsKey("Empty") ? 0 : addons.size()), addons);
//      return map;
//    }));
//    metrics.addCustomChart(new Metrics.AdvancedPie("addon_count", addonManager::getAddonCount));
  }

  @Override
  public void onDisable() {
    HandlerList.unregisterAll(this);
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
}
