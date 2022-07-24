package me.hsgamer.bettergui.action;

import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.api.menu.Menu;

import java.util.UUID;

public abstract class CommandAction extends BaseAction {
  /**
   * Create a new action
   *
   * @param menu   the menu
   * @param string the action string
   */
  protected CommandAction(Menu menu, String string) {
    super(menu, string);
  }

  /**
   * Get the final command to use in the dispatch method
   *
   * @param uuid the unique id
   *
   * @return the final command
   */
  protected String getFinalCommand(UUID uuid) {
    String command = getReplacedString(uuid);
    return command.startsWith("/") ? command : "/" + command;
  }
}