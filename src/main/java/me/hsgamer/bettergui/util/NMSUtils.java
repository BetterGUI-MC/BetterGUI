package me.hsgamer.bettergui.util;

import java.lang.reflect.InvocationTargetException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMSUtils {

  private static final String NMS_VERSION;

  static {
    String packageName = Bukkit.getServer().getClass().getPackage().getName();
    NMS_VERSION = packageName.substring(packageName.lastIndexOf('.') + 1);
  }

  private NMSUtils() {
  }

  public static String getNMSVersion() {
    return NMS_VERSION;
  }

  public static Class<?> getNMSClass(String name) throws ClassNotFoundException {
    return Class.forName("net.minecraft.server." + NMSUtils.getNMSVersion() + "." + name);
  }

  public static Class<?> getCraftBukkitClass(String name) throws ClassNotFoundException {
    return Class.forName("org.bukkit.craftbukkit." + NMSUtils.getNMSVersion() + "." + name);
  }

  public static void sendPacket(Player player, Object packet)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
    Object handle = player.getClass().getMethod("getHandle").invoke(player);
    Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
    playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"))
        .invoke(playerConnection, packet);
  }

}
