package me.hsgamer.bettergui.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class QuitListener implements Listener {
  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    getInstance().getMenuManager().getMenuNames().forEach(name -> getInstance().getMenuManager().getMenu(name).closeInventory(event.getPlayer()));
  }
}
