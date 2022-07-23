package me.hsgamer.bettergui.manager;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.bukkit.command.CommandManager;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class MenuCommandManager {
  private final Map<String, Command> registeredMenuCommand = new HashMap<>();
  private final BetterGUI plugin;

  public MenuCommandManager(BetterGUI plugin) {
    this.plugin = plugin;
  }

  /**
   * Register the command that opens the menu
   *
   * @param command the name of the command
   * @param menu    the menu
   */
  public void registerMenuCommand(String command, Menu menu) {
    registerMenuCommand(new BukkitCommand(command) {
      @Override
      public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender instanceof Player) {
          menu.create((Player) commandSender, strings, commandSender.hasPermission(Permissions.OPEN_MENU_BYPASS));
        } else {
          MessageUtils.sendMessage(commandSender, plugin.getMessageConfig().playerOnly);
        }
        return true;
      }
    });
  }

  /**
   * Register the command that opens the menu
   *
   * @param command the menu command
   */
  public void registerMenuCommand(Command command) {
    String name = command.getName();
    if (registeredMenuCommand.containsKey(name)) {
      getInstance().getLogger().log(Level.WARNING, "Duplicated \"{0}\" command ! Ignored", name);
      return;
    }
    CommandManager.registerCommandToCommandMap(getInstance().getName() + "_menu", command);
    registeredMenuCommand.put(name, command);
  }

  /**
   * Clear all menu commands
   */
  public void clearMenuCommand() {
    registeredMenuCommand.values().forEach(command -> {
      try {
        CommandManager.unregisterFromKnownCommands(command);
      } catch (IllegalAccessException e) {
        getInstance().getLogger().log(Level.WARNING, "Something wrong when unregister the command", e);
      }
    });
    registeredMenuCommand.clear();
  }

  /**
   * Get registered menu commands
   *
   * @return the commands
   */
  public Map<String, Command> getRegisteredMenuCommand() {
    return registeredMenuCommand;
  }
}
