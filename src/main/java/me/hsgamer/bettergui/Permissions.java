package me.hsgamer.bettergui;

import static me.hsgamer.bettergui.BetterGUI.getInstance;
import static me.hsgamer.hscore.bukkit.utils.PermissionUtils.createPermission;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public final class Permissions {

  public static final String PREFIX = getInstance().getName().toLowerCase();

  public static Permission OPEN_MENU;
  public static Permission RELOAD;
  public static Permission ADDONS;
  public static Permission HELP;
  public static Permission OPEN_MENU_BYPASS;
  public static Permission ADDON_DOWNLOADER;

  private Permissions() {

  }

  public static void init() {
    OPEN_MENU = createPermission(PREFIX + ".openmenu", null,
            PermissionDefault.OP);
    RELOAD = createPermission(PREFIX + ".reload", null,
            PermissionDefault.OP);
    ADDONS = createPermission(PREFIX + ".addons", null,
            PermissionDefault.OP);
    HELP = createPermission(PREFIX + ".help", null,
            PermissionDefault.OP);
    OPEN_MENU_BYPASS = createPermission(PREFIX + ".openmenu.bypass",
            null,
            PermissionDefault.OP);
    ADDON_DOWNLOADER = createPermission(PREFIX + ".addons.downloader",
            null, PermissionDefault.OP);
  }
}
