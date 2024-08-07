package me.hsgamer.bettergui.papi;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;

public class ExtraPlaceholderExpansion extends PlaceholderExpansion implements Loadable {
  private final BetterGUI plugin;

  public ExtraPlaceholderExpansion(BetterGUI plugin) {
    this.plugin = plugin;
  }

  @Override
  public void enable() {
    register();
  }

  @Override
  public void disable() {
    unregister();
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
      VariableManager.GLOBAL.addExternalReplacer(StringReplacer.of(
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