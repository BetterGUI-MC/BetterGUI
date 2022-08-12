package me.hsgamer.bettergui.util;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.Collection;

/**
 * The utility class for player
 */
public final class PlayerUtil {
  private PlayerUtil() {
    // EMPTY
  }

  /**
   * Check if the player has one of the permissions
   *
   * @param player      the player
   * @param permissions the permissions
   *
   * @return true if the player does
   */
  public static boolean hasAnyPermission(Player player, Collection<Permission> permissions) {
    for (Permission permission : permissions) {
      if (player.hasPermission(permission)) {
        return true;
      }
    }
    return false;
  }
}
