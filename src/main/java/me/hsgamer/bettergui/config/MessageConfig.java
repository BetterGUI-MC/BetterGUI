package me.hsgamer.bettergui.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.annotated.AnnotatedConfig;
import me.hsgamer.hscore.config.annotation.ConfigPath;
import org.bukkit.plugin.Plugin;

/**
 * The config for messages
 */
public class MessageConfig extends AnnotatedConfig {
  public final @ConfigPath("prefix") String prefix;
  public final @ConfigPath("no-permission") String noPermission;
  public final @ConfigPath("player-only") String playerOnly;
  public final @ConfigPath("success") String success;
  public final @ConfigPath("menu-required") String menuRequired;
  public final @ConfigPath("menu-not-found") String menuNotFound;
  public final @ConfigPath("player-not-found") String playerNotFound;
  public final @ConfigPath("empty-arg-value") String emptyArgValue;

  public MessageConfig(Plugin plugin) {
    super(new BukkitConfig(plugin, "messages.yml"));

    prefix = "&f[&bBetterGUI&f] ";
    noPermission = "&cYou don't have permission to do this";
    playerOnly = "&cYou must be a player to do this";
    success = "&aSuccess";
    menuRequired = "&cYou should specify a menu";
    menuNotFound = "&cThat menu does not exist";
    playerNotFound = "&cThe player is not found. Maybe he is offline or didn't join your server";
    emptyArgValue = "/empty/";
  }
}
