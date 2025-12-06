package me.hsgamer.bettergui.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.manager.VariableManager;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.StringReplacer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;

public class MenuPlaceholderExpansion extends PlaceholderExpansion {
  private final BetterGUI plugin;

  public MenuPlaceholderExpansion(BetterGUI plugin) {
    this.plugin = plugin;
  }

  @Override
  public @NotNull String getIdentifier() {
    return plugin.getName().toLowerCase(Locale.ROOT);
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public boolean register() {
    boolean success = super.register();
    if (success) {
      plugin.get(VariableManager.class).addExternalReplacer(StringReplacer.of(
        (original) -> {
          try {
            return PlaceholderAPI.setPlaceholders(null, original);
          } catch (Throwable throwable) {
            plugin.getLogger().log(Level.WARNING, "Error while replacing placeholders", throwable);
            return original;
          }
        },
        (original, uuid) -> {
          try {
            return PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(uuid), original);
          } catch (Throwable throwable) {
            plugin.getLogger().log(Level.WARNING, "Error while replacing placeholders", throwable);
            return original;
          }
        }
      ));
    }
    return success;
  }

  @Override
  public @NotNull String getAuthor() {
    return Arrays.toString(plugin.getDescription().getAuthors().toArray());
  }

  @Override
  public @NotNull String getVersion() {
    return plugin.getDescription().getVersion();
  }

  @Override
  public String onRequest(OfflinePlayer player, @NotNull String identifier) {
    return StringReplacerApplier.replace(StringReplacerApplier.normalizeQuery(identifier), player.getUniqueId(), true);
  }
}