package me.hsgamer.bettergui;

import io.github.projectunified.minelib.plugin.base.BasePlugin;
import io.github.projectunified.minelib.plugin.permission.PermissionComponent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public final class Permissions extends PermissionComponent {

  public static final String PREFIX = "bettergui";

  public static final Permission OPEN_MENU = new Permission(PREFIX + ".openmenu", PermissionDefault.OP);
  public static final Permission RELOAD = new Permission(PREFIX + ".reload", PermissionDefault.OP);
  public static final Permission ADDONS = new Permission(PREFIX + ".addons", PermissionDefault.OP);
  public static final Permission HELP = new Permission(PREFIX + ".help", PermissionDefault.OP);
  public static final Permission VARIABLE = new Permission(PREFIX + ".variable", PermissionDefault.OP);
  public static final Permission TEMPLATE_BUTTON = new Permission(PREFIX + ".templatebuttons", PermissionDefault.OP);
  public static final Permission OPEN_MENU_BYPASS = new Permission(PREFIX + ".openmenu.bypass", PermissionDefault.OP);

  public Permissions(BasePlugin plugin) {
    super(plugin);
  }
}
