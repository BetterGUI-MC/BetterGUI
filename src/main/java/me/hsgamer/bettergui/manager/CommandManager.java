package me.hsgamer.bettergui.manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager {

  private final HashMap<String, BukkitCommand> registered = new HashMap<>();
  private final HashMap<String, BukkitCommand> registeredMenuCommand = new HashMap<>();
  private final JavaPlugin plugin;
  private Field knownCommandsField;
  private CommandMap bukkitCommandMap;

  public CommandManager(JavaPlugin plugin) {
    this.plugin = plugin;
    try {
      Method getCommandMapMethod = Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap");
      bukkitCommandMap = (CommandMap) getCommandMapMethod.invoke(Bukkit.getServer());

      knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
      knownCommandsField.setAccessible(true);
    } catch (ReflectiveOperationException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  /**
   * Register new command
   *
   * @param command the command object
   */
  public void register(BukkitCommand command) {
    String name = command.getLabel();
    if (registered.containsKey(name)) {
      plugin.getLogger().log(Level.WARNING, "Duplicated \"{0}\" command ! Ignored", name);
      return;
    }

    bukkitCommandMap.register(plugin.getName(), command);
    registered.put(name, command);
  }

  /**
   * Unregister new command
   *
   * @param command the command object
   */
  public void unregister(BukkitCommand command) {
    try {
      Map<?, ?> knownCommands = (Map<?, ?>) knownCommandsField.get(bukkitCommandMap);

      knownCommands.values().removeIf(command::equals);

      command.unregister(bukkitCommandMap);
      registered.remove(command.getLabel());
    } catch (ReflectiveOperationException e) {
      plugin.getLogger()
          .log(Level.WARNING, "Something wrong when unregister the command", e);
    }
  }

  /**
   * Register the command that opens the menu
   *
   * @param command the name of the command
   * @param menu    the menu
   */
  public void registerMenuCommand(String command, Menu menu) {
    if (registeredMenuCommand.containsKey(command)) {
      plugin.getLogger().log(Level.WARNING, "Duplicated \"{0}\" command ! Ignored", command);
      return;
    }
    BukkitCommand bukkitCommand = new BukkitCommand(command) {
      @Override
      public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender instanceof Player) {
          menu.createInventory((Player) commandSender);
        } else {
          CommonUtils.sendMessage(commandSender, BetterGUI.getInstance().getMessageConfig().get(
              DefaultMessage.PLAYER_ONLY));
        }
        return true;
      }
    };
    bukkitCommandMap.register(plugin.getName() + "_menu", bukkitCommand);
    registeredMenuCommand.put(command, bukkitCommand);
  }

  public void clearMenuCommand() {
    registeredMenuCommand.values().forEach(this::unregister);
    registeredMenuCommand.clear();
  }
}
