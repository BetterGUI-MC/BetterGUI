package me.hsgamer.bettergui;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Permissions {

  private static final String PREFIX = BetterGUI.getInstance().getDescription().getName()
      .toLowerCase();

  public static final Permission ITEMS = new Permission(PREFIX, ".items", PermissionDefault.OP);
  public static final Permission OPEN_MENU = new Permission(PREFIX, ".openmenu",
      PermissionDefault.OP);
  public static final Permission RELOAD = new Permission(PREFIX, ".reload",
      PermissionDefault.OP);
  public static final Permission ADDONS = new Permission(PREFIX, ".addons",
      PermissionDefault.OP);

  private Permissions() {

  }
}
