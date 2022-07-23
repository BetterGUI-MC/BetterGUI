package me.hsgamer.bettergui;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import static me.hsgamer.bettergui.BetterGUI.getInstance;
import static org.bukkit.Bukkit.getPluginManager;

public final class Permissions {

  public static final String PREFIX = getInstance().getName().toLowerCase();

  public static final Permission OPEN_MENU = new Permission(PREFIX + ".openmenu", PermissionDefault.OP);
  public static final Permission RELOAD = new Permission(PREFIX + ".reload", PermissionDefault.OP);
  public static final Permission ADDONS = new Permission(PREFIX + ".addons", PermissionDefault.OP);
  public static final Permission HELP = new Permission(PREFIX + ".help", PermissionDefault.OP);
  public static final Permission VARIABLE = new Permission(PREFIX + ".variable", PermissionDefault.OP);
  public static final Permission TEMPLATE_BUTTON = new Permission(PREFIX + ".templatebuttons", PermissionDefault.OP);
  public static final Permission OPEN_MENU_BYPASS = new Permission(PREFIX + ".openmenu.bypass", PermissionDefault.OP);
  public static final Permission ADDON_DOWNLOADER = new Permission(PREFIX + ".addons.downloader", PermissionDefault.OP);

  private Permissions() {

  }

  public static void register() {
    getPluginManager().addPermission(OPEN_MENU);
    getPluginManager().addPermission(RELOAD);
    getPluginManager().addPermission(ADDONS);
    getPluginManager().addPermission(HELP);
    getPluginManager().addPermission(VARIABLE);
    getPluginManager().addPermission(TEMPLATE_BUTTON);
    getPluginManager().addPermission(OPEN_MENU_BYPASS);
    getPluginManager().addPermission(ADDON_DOWNLOADER);
  }

  public static void unregister() {
    getPluginManager().removePermission(OPEN_MENU);
    getPluginManager().removePermission(RELOAD);
    getPluginManager().removePermission(ADDONS);
    getPluginManager().removePermission(HELP);
    getPluginManager().removePermission(VARIABLE);
    getPluginManager().removePermission(TEMPLATE_BUTTON);
    getPluginManager().removePermission(OPEN_MENU_BYPASS);
    getPluginManager().removePermission(ADDON_DOWNLOADER);
  }
}
