package me.hsgamer.bettergui.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.hsgamer.bettergui.object.Command;
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
import me.hsgamer.bettergui.object.variable.LocalVariableManager;

public final class CommandBuilder {

  private static final Map<Pattern, Function<String, Command>> commands = new HashMap<>();

  static {
    register("console:", ConsoleCommand::new);
    register("op:", OpCommand::new);
    register("player:", PlayerCommand::new);
    register("delay:", DelayCommand::new);
    register("condition:", ConditionCommand::new);
    register("(open|menu|open-?menu):", OpenMenuCommand::new);
    register("back-?menu", BackCommand::new);
    register("tell:", TellCommand::new);
    register("broadcast:", BroadcastCommand::new);
    register("close-?menu", CloseMenuCommand::new);
    register("update-?menu", UpdateMenuCommand::new);
    register("permission:", PermissionCommand::new);
  }

  private CommandBuilder() {

  }

  /**
   * Register new command type
   *
   * @param regex           the regex that detects the prefix of the string
   * @param commandFunction the "create command" function
   */
  public static void register(String regex, Function<String, Command> commandFunction) {
    Pattern pattern = Pattern.compile("^(?i)" + regex, Pattern.CASE_INSENSITIVE);
    commands.put(pattern, commandFunction);
  }

  /**
   * Register new command type
   *
   * @param regex the regex that detects the prefix of the string
   * @param clazz the class
   * @deprecated use {@link #register(String, Function)} instead
   */
  @Deprecated
  public static void register(String regex, Class<? extends Command> clazz) {
    Pattern pattern = Pattern.compile("^(?i)" + regex, Pattern.CASE_INSENSITIVE);
    commands.put(pattern, s -> {
      try {
        return clazz.getDeclaredConstructor(String.class)
            .newInstance(s);
      } catch (Exception e) {
        throw new RuntimeException("Invalid command class");
      }
    });
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
    for (Entry<Pattern, Function<String, Command>> entry : commands.entrySet()) {
      Matcher matcher = entry.getKey().matcher(input);
      if (matcher.find()) {
        Command command = entry.getValue().apply(matcher.replaceFirst("").trim());
        command.setVariableManager(localVariableManager);
        return command;
      }
    }

    Command command = new PlayerCommand(input);
    command.setVariableManager(localVariableManager);
    return command;
  }
}
