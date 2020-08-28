package me.hsgamer.bettergui.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandManager extends me.hsgamer.hscore.bukkit.command.CommandManager {

  private final Map<String, Command> registeredMenuCommand = new HashMap<>();

  public CommandManager(JavaPlugin plugin) {
    super(plugin);
  }

  /**
   * Register the command that opens the menu
   *
   * @param command the name of the command
   * @param menu    the menu
   */
  public void registerMenuCommand(String command, Menu<?> menu) {
    registerMenuCommand(new BukkitCommand(command) {
      @Override
      public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender instanceof Player) {
          menu.createInventory((Player) commandSender, strings,
              commandSender.hasPermission(Permissions.OPEN_MENU_BYPASS));
        } else {
          MessageUtils.sendMessage(commandSender, MessageConfig.PLAYER_ONLY.getValue());
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
      plugin.getLogger().log(Level.WARNING, "Duplicated \"{0}\" command ! Ignored", name);
      return;
    }
    bukkitCommandMap.register(plugin.getName() + "_menu", command);
    registeredMenuCommand.put(name, command);
  }

  public void clearMenuCommand() {
    registeredMenuCommand.values().forEach(this::unregister);
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
