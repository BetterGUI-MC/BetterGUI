package me.hsgamer.bettergui.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.LocalVariableManager;
import me.hsgamer.bettergui.object.command.BackCommand;
import me.hsgamer.bettergui.object.command.BroadcastCommand;
import me.hsgamer.bettergui.object.command.CloseMenuCommand;
import me.hsgamer.bettergui.object.command.ConditionCommand;
import me.hsgamer.bettergui.object.command.ConsoleCommand;
import me.hsgamer.bettergui.object.command.DelayCommand;
import me.hsgamer.bettergui.object.command.OpCommand;
import me.hsgamer.bettergui.object.command.OpenMenuCommand;
import me.hsgamer.bettergui.object.command.PermissionCommand;
import me.hsgamer.bettergui.object.command.PlayerCommand;
import me.hsgamer.bettergui.object.command.TellCommand;
import me.hsgamer.bettergui.object.command.UpdateMenuCommand;

public final class CommandBuilder {

  private static final Map<Pattern, Class<? extends Command>> commands = new HashMap<>();

  static {
    register("console:", ConsoleCommand.class);
    register("op:", OpCommand.class);
    register("player:", PlayerCommand.class);
    register("delay:", DelayCommand.class);
    register("condition:", ConditionCommand.class);
    register("(open|menu|open-?menu):", OpenMenuCommand.class);
    register("back-?menu", BackCommand.class);
    register("tell:", TellCommand.class);
    register("broadcast:", BroadcastCommand.class);
    register("close-?menu", CloseMenuCommand.class);
    register("update-?menu", UpdateMenuCommand.class);
    register("permission:", PermissionCommand.class);
  }

  private CommandBuilder() {

  }

  /**
   * Register new command type
   *
   * @param regex the regex that detects the prefix of the string
   * @param clazz the class
   */
  public static void register(String regex, Class<? extends Command> clazz) {
    Pattern pattern = Pattern.compile("^(?i)" + regex, Pattern.CASE_INSENSITIVE);
    commands.put(pattern, clazz);
  }

  public static List<Command> getCommands(LocalVariableManager<?> localVariableManager,
      List<String> input) {
    input.replaceAll(String::trim);

    List<Command> list = new ArrayList<>();
    input.forEach(string -> list.add(getCommand(localVariableManager, string)));
    return list;
  }


  /**
   * Get Command object from a String
   *
   * @param localVariableManager the local variable manager that involves the command
   * @param input                the command string
   * @return Command Object
   */
  public static Command getCommand(LocalVariableManager<?> localVariableManager, String input) {
    for (Entry<Pattern, Class<? extends Command>> entry : commands.entrySet()) {
      Matcher matcher = entry.getKey().matcher(input);
      if (matcher.find()) {
        String cleanCommand = matcher.replaceFirst("").trim();

        try {
          Command command = entry.getValue().getDeclaredConstructor(String.class)
              .newInstance(cleanCommand);
          command.setVariableManager(localVariableManager);
          return command;
        } catch (Exception e) {
          // Checked at startup
        }
      }
    }

    Command command = new PlayerCommand(input);
    command.setVariableManager(localVariableManager);
    return command;
  }
}
