package me.hsgamer.bettergui.util;

public class VersionUtils {

  private VersionUtils() {

  }

  public static boolean isSpigot() {
    return Validate.isClassLoaded("org.bukkit.entity.Player$Spigot");
  }

}
