package me.hsgamer.bettergui;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Permissions {

  private static final String PREFIX = getInstance().getName()
      .toLowerCase();

  public static final Permission ITEMS = createPermission(PREFIX + ".items", null,
      PermissionDefault.OP);
  public static final Permission OPEN_MENU = createPermission(PREFIX + ".openmenu", null,
      PermissionDefault.OP);
  public static final Permission RELOAD = createPermission(PREFIX + ".reload", null,
      PermissionDefault.OP);
  public static final Permission ADDONS = createPermission(PREFIX + ".addons", null,
      PermissionDefault.OP);
  public static final Permission HELP = createPermission(PREFIX + ".help", null,
      PermissionDefault.OP);

  private Permissions() {

  }

  public static Permission createPermission(String name, String description,
      PermissionDefault permissionDefault) {
    Permission permission = new Permission(name);
    if (description != null) {
      permission.setDescription(description);
    }
    if (permissionDefault != null) {
      permission.setDefault(permissionDefault);
    }
    getInstance().getServer().getPluginManager().removePermission(permission);
    return permission;
  }
}
