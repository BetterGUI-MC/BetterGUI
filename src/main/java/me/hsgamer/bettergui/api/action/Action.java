package me.hsgamer.bettergui.api.action;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.menu.MenuElement;

import java.util.UUID;

/**
 * The action used in Menus/Buttons/...
 */
public interface Action extends MenuElement {
  /**
   * Add the executable code to taskChain
   *
   * @param uuid      the unique id
   * @param taskChain the TaskChain that needs adding
   */
  void addToTaskChain(UUID uuid, TaskChain<?> taskChain);

  /**
   * Set the menu involved in the action
   *
   * @param menu the menu
   */
  void setMenu(Menu menu);
}
