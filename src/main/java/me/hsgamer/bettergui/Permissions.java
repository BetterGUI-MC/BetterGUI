package me.hsgamer.bettergui;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import static me.hsgamer.bettergui.BetterGUI.getInstance;
import static me.hsgamer.hscore.bukkit.utils.PermissionUtils.createPermission;

public final class Permissions {

  public static final String PREFIX = getInstance().getName().toLowerCase();

  public static final Permission OPEN_MENU = createPermission(PREFIX + ".openmenu", null, PermissionDefault.OP);
  public static final Permission RELOAD = createPermission(PREFIX + ".reload", null, PermissionDefault.OP);
  public static final Permission ADDONS = createPermission(PREFIX + ".addons", null, PermissionDefault.OP);
  public static final Permission HELP = createPermission(PREFIX + ".help", null, PermissionDefault.OP);
  public static final Permission OPEN_MENU_BYPASS = createPermission(PREFIX + ".openmenu.bypass", null, PermissionDefault.OP);
  public static final Permission ADDON_DOWNLOADER = createPermission(PREFIX + ".addons.downloader", null, PermissionDefault.OP);

  private Permissions() {

  }
}
