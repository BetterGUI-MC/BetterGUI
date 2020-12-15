package me.hsgamer.bettergui.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.Locale;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class BetterGUIPlaceholderExpansion extends PlaceholderExpansion {
  @Override
  public String getIdentifier() {
    return getInstance().getName().toLowerCase(Locale.ROOT);
  }

  @Override
  public String getAuthor() {
    return Arrays.toString(getInstance().getDescription().getAuthors().toArray());
  }

  @Override
  public String getVersion() {
    return getInstance().getDescription().getVersion();
  }

  @Override
  public String onRequest(OfflinePlayer player, String identifier) {
    return VariableManager.setVariables("{" + identifier + "}", player.getUniqueId());
  }
}
