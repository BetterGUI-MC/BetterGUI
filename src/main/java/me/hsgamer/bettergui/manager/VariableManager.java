package me.hsgamer.bettergui.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.hsgamer.bettergui.hook.PlaceholderAPIHook;
import me.hsgamer.bettergui.object.GlobalVariable;
import me.hsgamer.bettergui.util.BukkitUtils;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.ExpressionUtils;
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public final class VariableManager {

  private static final Pattern PATTERN = Pattern.compile("[{]([^{}]+)[}]");
  private static final Map<String, GlobalVariable> variables = new HashMap<>();

  static {
    register("player", (executor, identifier) -> executor.getName());
    register("online",
        (executor, identifier) -> String.valueOf(BukkitUtils.getOnlinePlayers().size()));
    register("max_players", (executor, identifier) -> String.valueOf(Bukkit.getMaxPlayers()));
    register("world", (executor, identifier) -> {
      if (executor.isOnline()) {
        return executor.getPlayer().getWorld().getName();
      }
      return "";
    });
    register("x", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getLocation().getX());
      }
      return "";
    });
    register("y", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getLocation().getY());
      }
      return "";
    });
    register("z", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getLocation().getZ());
      }
      return "";
    });
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
    register("exp", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getTotalExperience());
      }
      return "";
    });
    register("level", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getLevel());
      }
      return "";
    });
    register("exp_to_level", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getExpToLevel());
      }
      return "";
    });
    register("food_level", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getFoodLevel());
      }
      return "";
    });
    register("ip", (executor, identifier) -> {
      if (executor.isOnline()) {
        return executor.getPlayer().getAddress().getAddress().getHostAddress();
      }
      return "";
    });
    register("biome", (executor, identifier) -> {
      if (executor.isOnline()) {
        return String.valueOf(executor.getPlayer().getLocation().getBlock().getBiome());
      }
      return "";
    });
    register("ping", ((executor, identifier) -> {
      if (executor.isOnline()) {
        return BukkitUtils.getPing(executor.getPlayer());
      }
      return "";
    }));
    register("rainbow", (executor, identifier) -> {
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
      return CommonUtils.colorize("&" + color.getChar());
    });
    register("random_", (executor, identifier) -> {
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
    register("condition_", (executor, identifier) -> {
      if (ExpressionUtils.isValidExpression(identifier)) {
        return ExpressionUtils.getResult(identifier).toPlainString();
      }
      return null;
    });
    register("uuid", (executor, identifier) -> executor.getUniqueId().toString());
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
    if (isMatch(message, variables.keySet())) {
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
  public static String setVariables(String message, OfflinePlayer executor) {
    String old;
    do {
      old = message;
      message = setSingleVariables(message, executor, variables);
    } while (hasVariables(message) && !old.equals(message));
    if (PlaceholderAPIHook.hasValidPlugin()) {
      message = PlaceholderAPIHook.setPlaceholders(message, executor);
    }
    return message;
  }

  /**
   * Replace the local variables of the string
   *
   * @param message         the string
   * @param executor        the player involved in
   * @param globalVariables the map of variables
   * @return the replaced string
   */
  public static String setSingleVariables(String message, OfflinePlayer executor,
      Map<String, ? extends GlobalVariable> globalVariables) {
    Matcher matcher = PATTERN.matcher(message);
    while (matcher.find()) {
      String identifier = matcher.group(1).trim();
      for (Map.Entry<String, ? extends GlobalVariable> variable : globalVariables.entrySet()) {
        if (identifier.toLowerCase().startsWith(variable.getKey())) {
          String replace = variable.getValue()
              .getReplacement(executor, identifier.substring(variable.getKey().length()));
          if (replace != null) {
            message = message
                .replaceFirst(Pattern.quote(matcher.group()), Matcher.quoteReplacement(replace));
          }
        }
      }
    }
    return message;
  }

  public static boolean isMatch(String string, Collection<String> matchString) {
    Matcher matcher = PATTERN.matcher(string);
    List<String> found = new ArrayList<>();
    while (matcher.find()) {
      found.add(matcher.group(1).trim());
    }

    if (found.isEmpty()) {
      return false;
    } else {
      return found.stream().map(String::toLowerCase).parallel().anyMatch(s -> {
        for (String match : matchString) {
          if (s.startsWith(match)) {
            return true;
          }
        }
        return false;
      });
    }
  }
}
