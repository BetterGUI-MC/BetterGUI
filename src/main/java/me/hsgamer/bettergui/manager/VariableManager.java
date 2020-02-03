package me.hsgamer.bettergui.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.hsgamer.bettergui.hook.PlaceholderAPIHook;
import me.hsgamer.bettergui.object.GlobalVariable;
import me.hsgamer.bettergui.util.BukkitUtils;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VariableManager {

  private static final Pattern pattern = Pattern.compile("[{]([^{}]+)[}]");
  private static final Map<String, GlobalVariable> variables = new HashMap<>();

  static {
    register("player", (executor, identifier) -> executor.getName());
    register("online",
        (executor, identifier) -> String.valueOf(BukkitUtils.getOnlinePlayers().size()));
    register("max_players", (executor, identifier) -> String.valueOf(Bukkit.getMaxPlayers()));
    register("world", (executor, identifier) -> executor.getWorld().getName());
    register("x", (executor, identifier) -> String.valueOf(executor.getLocation().getX()));
    register("y", (executor, identifier) -> String.valueOf(executor.getLocation().getY()));
    register("z", (executor, identifier) -> String.valueOf(executor.getLocation().getZ()));
    register("bed_", ((executor, identifier) -> {
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
    register("exp", (executor, identifier) -> String.valueOf(executor.getTotalExperience()));
    register("level", (executor, identifier) -> String.valueOf(executor.getLevel()));
    register("exp_to_level", (executor, identifier) -> String.valueOf(executor.getExpToLevel()));
    register("food_level", (executor, identifier) -> String.valueOf(executor.getFoodLevel()));
    register("ip", (executor, identifier) -> executor.getAddress().getAddress().getHostAddress());
    register("biome",
        (executor, identifier) -> String.valueOf(executor.getLocation().getBlock().getBiome()));
    register("ping", ((executor, identifier) -> BukkitUtils.getPing(executor)));
    register("rainbow", (executor, identifier) -> {
      ChatColor[] values = ChatColor.values();
      ChatColor color = null;
      while (color == null
          || color.equals(ChatColor.BOLD)
          || color.equals(ChatColor.ITALIC)
          || color.equals(ChatColor.STRIKETHROUGH)
          || color.equals(ChatColor.RESET)
          || color.equals(ChatColor.MAGIC)
          || color.equals(ChatColor.UNDERLINE)) {
        color = values[ThreadLocalRandom.current().nextInt(values.length - 1)];
      }
      return CommonUtils.colorize("&" + color.getChar());
    });
  }

  private VariableManager() {

  }

  /**
   * Register new variable
   *
   * @param prefix   the prefix
   * @param variable the Variable object
   */
  public static void register(String prefix, GlobalVariable variable) {
    variables.put(prefix, variable);
  }

  /**
   * Check if a string contains variables
   *
   * @param message the string
   * @return true if it has, otherwise false
   */
  public static boolean hasVariables(String message) {
    if (message == null || message.trim().isEmpty()) {
      return false;
    }
    if (Validate.isMatch(message, pattern, variables.keySet())) {
      return true;
    }
    return PlaceholderAPIHook.hasValidPlugin() && PlaceholderAPIHook.hasPlaceholders(message);
  }

  /**
   * Replace the variables of the string
   *
   * @param message  the string
   * @param executor the player involved in
   * @return the replaced string
   */
  public static String setVariables(String message, Player executor) {
    Matcher matcher = pattern.matcher(message);
    while (matcher.find()) {
      String identifier = matcher.group(1).trim();
      for (Map.Entry<String, GlobalVariable> variable : variables.entrySet()) {
        if (identifier.startsWith(variable.getKey())) {
          String replace = variable.getValue()
              .getReplacement(executor, identifier.substring(variable.getKey().length()));
          if (replace != null) {
            message = message
                .replaceAll(Pattern.quote(matcher.group()), Matcher.quoteReplacement(replace));
          }
        }
      }
    }
    if (PlaceholderAPIHook.hasValidPlugin()) {
      message = PlaceholderAPIHook.setPlaceholders(message, executor);
    }
    return message;
  }
}
