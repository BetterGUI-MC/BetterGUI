package me.hsgamer.bettergui.manager;

import me.hsgamer.hscore.bukkit.utils.BukkitUtils;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import me.hsgamer.hscore.expression.ExpressionUtils;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The variable manager for the plugin
 */
public class PluginVariableManager {
  private static final Set<String> pluginVariables = new HashSet<>();

  private PluginVariableManager() {
    // EMPTY
  }

  /**
   * Register the variable
   *
   * @param prefix         the prefix
   * @param stringReplacer the string replacer
   */
  public static void register(String prefix, StringReplacer stringReplacer) {
    pluginVariables.add(prefix);
    VariableManager.register(prefix, stringReplacer);
  }

  /**
   * Unregister the variable
   *
   * @param prefix the prefix
   */
  public static void unregister(String prefix) {
    pluginVariables.remove(prefix);
    VariableManager.unregister(prefix);
  }

  /**
   * Unregister all plugin variables
   */
  public static void unregisterAll() {
    pluginVariables.forEach(VariableManager::unregister);
    pluginVariables.clear();
  }

  /**
   * Register default variables
   */
  public static void registerDefaultVariables() {
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
    VariableManager.register("rainbow", (original, uuid) -> MessageUtils.getRandomColor().toString());

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
}
