package me.hsgamer.bettergui;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Permissions {

  private static String prefix = BetterGUI.getInstance().getDescription().getName().toLowerCase();

  public static final Permission ITEMS = new Permission(prefix, ".items", PermissionDefault.OP);
  public static final Permission OPEN_MENU = new Permission(prefix, ".openmenu",
      PermissionDefault.OP);
  public static final Permission RELOAD = new Permission(prefix, ".reload",
      PermissionDefault.OP);

  private Permissions() {

  }
}
