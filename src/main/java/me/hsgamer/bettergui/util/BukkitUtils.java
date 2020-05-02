package me.hsgamer.bettergui.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.stream.IntStream;
import me.hsgamer.bettergui.BetterGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class BukkitUtils {

  private static final Method LEGACY_GET_ONLINE_PLAYERS;

  static {
    try {
      Method method = Bukkit.class.getDeclaredMethod("getOnlinePlayers");
      LEGACY_GET_ONLINE_PLAYERS = method.getReturnType() == Player[].class ? method : null;
    } catch (NoSuchMethodException e) {
      // This should NEVER happen!
      throw new IllegalStateException("Missing Bukkit.getOnlinePlayers() method!");
    }
  }

  private BukkitUtils() {
  }

  /**
   * Get online players
   *
   * @return the list of player
   */
  public static Collection<? extends Player> getOnlinePlayers() {
    try {
      if (LEGACY_GET_ONLINE_PLAYERS == null) {
        return Bukkit.getOnlinePlayers();
      } else {
        Player[] playersArray = (Player[]) LEGACY_GET_ONLINE_PLAYERS.invoke(null);
        return Arrays.asList(playersArray);
      }
    } catch (Exception e) {
      BetterGUI.getInstance().getLogger()
          .log(Level.WARNING, "Unexpected error when getting online players", e);
      return Collections.emptyList();
    }
  }

  /**
   * Get ping
   *
   * @param player the player
   * @return the ping of the player
   */
  public static String getPing(Player player) {
    try {
      Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
      return String.valueOf(entityPlayer.getClass().getField("ping").getInt(entityPlayer));
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
      BetterGUI.getInstance().getLogger()
          .log(Level.WARNING, "Unexpected error when getting ping", e);
      return "ERROR GETTING PING";
    }
  }

  /**
   * Check if the server is Spigot
   *
   * @return whether the server is Spigot
   */
  public static boolean isSpigot() {
    return Validate.isClassLoaded("org.bukkit.entity.Player$Spigot");
  }

  /**
   * Get empty slots of the inventory
   *
   * @param inventory the inventory
   * @return a IntStream of empty slots
   */
  public static IntStream getEmptySlots(Inventory inventory) {
    return IntStream
        .range(0, inventory.getSize())
        .filter(slot -> inventory.getItem(slot) != null);
  }
}
