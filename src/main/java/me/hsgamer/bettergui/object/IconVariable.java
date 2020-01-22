package me.hsgamer.bettergui.object;

import org.bukkit.entity.Player;

public interface IconVariable {

  String getIdentifier();

  Icon getIcon();

  String getReplacement(Player executor, String identifier);
}
