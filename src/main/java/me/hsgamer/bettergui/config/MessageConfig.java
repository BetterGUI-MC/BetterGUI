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

  public MessageConfig(Plugin plugin) {
    super(new BukkitConfig(plugin, "messages.yml"));

    prefix = "&f[&bBetterGUI&f] ";
    noPermission = "&cYou don't have permission to do this";
    playerOnly = "&cYou must be a player to do this";
    success = "&aSuccess";
  }
}
