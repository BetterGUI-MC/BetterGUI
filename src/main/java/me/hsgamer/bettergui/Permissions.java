package me.hsgamer.bettergui;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Permissions {

  private static String prefix = BetterGUI.getInstance().getDescription().getName().toLowerCase();

  public static final Permission ITEMS = new Permission(prefix, ".items", PermissionDefault.OP);

  private Permissions() {

  }
}
