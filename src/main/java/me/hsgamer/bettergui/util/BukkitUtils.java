package me.hsgamer.bettergui.util;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import org.bukkit.entity.Player;

public final class BukkitUtils {

  private BukkitUtils() {
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
}
