package me.hsgamer.bettergui.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import io.github.projectunified.minelib.plugin.command.CommandComponent;
import io.github.projectunified.minelib.plugin.postenable.PostEnable;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MenuCommandManager implements Loadable, PostEnable {
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
    registerMenuCommand(new Command(command) {
      @Override
      public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender instanceof Player) {
          menu.create((Player) commandSender, strings, commandSender.hasPermission(Permissions.OPEN_MENU_BYPASS));
          return true;
        } else {
          MessageUtils.sendMessage(commandSender, plugin.get(MessageConfig.class).getPlayerOnly());
          return false;
        }
      }

      @Override
      public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (sender instanceof Player) {
          return menu.tabComplete((Player) sender, args);
        }
        return Collections.emptyList();
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
      plugin.getLogger().log(Level.WARNING, "Duplicated \"{0}\" command ! Ignored", name);
      return;
    }
    CommandComponent.registerCommandToCommandMap(plugin.getName() + "_menu", command);
    registeredMenuCommand.put(name, command);
  }

  /**
   * Clear all menu commands
   */
  public void clearMenuCommand() {
    registeredMenuCommand.values().forEach(CommandComponent::unregisterFromKnownCommands);
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

  @Override
  public void postEnable() {
    CommandComponent.syncCommand();
  }

  @Override
  public void disable() {
    clearMenuCommand();
  }
}
