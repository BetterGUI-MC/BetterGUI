package me.hsgamer.bettergui.manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager {

  private Field knownCommandsField;
  private CommandMap bukkitCommandMap;
  private HashMap<String, BukkitCommand> registered = new HashMap<>();
  private JavaPlugin plugin;

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

  public void register(BukkitCommand command) {
    String name = command.getLabel();
    if (registered.containsValue(command)) {
      Bukkit.getConsoleSender().sendMessage(
          ChatColor.RED + "Duplicated " + ChatColor.WHITE + name + ChatColor.RED + " ! Ignored");
      return;
    }

    bukkitCommandMap.register(plugin.getName(), command);
    registered.put(name, command);
  }

  public void unregister(BukkitCommand command) {
    try {
      Map<?, ?> knownCommands = (Map<?, ?>) knownCommandsField.get(bukkitCommandMap);

      knownCommands.values().removeIf(command::equals);

      command.unregister(bukkitCommandMap);
    } catch (ReflectiveOperationException e) {
      plugin.getLogger()
          .log(Level.WARNING, "Something wrong when unregister the command", e);
    }
  }
}
