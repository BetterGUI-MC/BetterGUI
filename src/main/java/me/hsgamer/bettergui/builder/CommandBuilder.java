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
import me.hsgamer.bettergui.object.command.MusicCommand;
import me.hsgamer.bettergui.object.command.OpCommand;
import me.hsgamer.bettergui.object.command.OpenMenuCommand;
import me.hsgamer.bettergui.object.command.PermissionCommand;
import me.hsgamer.bettergui.object.command.PlayerCommand;
import me.hsgamer.bettergui.object.command.RawSoundCommand;
import me.hsgamer.bettergui.object.command.SoundCommand;
import me.hsgamer.bettergui.object.command.TellCommand;
import me.hsgamer.bettergui.object.command.UpdateMenuCommand;
import me.hsgamer.bettergui.object.variable.LocalVariableManager;

public final class CommandBuilder {

  private static final Map<Pattern, Function<String, Command>> commands = new HashMap<>();

  static {
    register(ConsoleCommand::new, "console:");
    register(OpCommand::new, "op:");
    register(PlayerCommand::new, "player:");
    register(DelayCommand::new, "delay:");
    register(ConditionCommand::new, "condition:");
    register(OpenMenuCommand::new, "(open|menu|open-?menu):");
    register(BackCommand::new, "back-?menu");
    register(TellCommand::new, "tell:");
    register(BroadcastCommand::new, "broadcast:");
    register(CloseMenuCommand::new, "close-?menu");
    register(UpdateMenuCommand::new, "update-?menu");
    register(PermissionCommand::new, "permission:");
    register(SoundCommand::new, "sound:");
    register(RawSoundCommand::new, "raw-sound:");
    register(MusicCommand::new, "music:");
  }

  private CommandBuilder() {

  }

  /**
   * Register new command type
   *
   * @param commandFunction the "create command" function
   * @param regex           the regex that detects the prefix of the string
   */
  public static void register(Function<String, Command> commandFunction, String... regex) {
    for (String s : regex) {
      Pattern pattern = Pattern.compile("^(?i)" + s, Pattern.CASE_INSENSITIVE);
      commands.put(pattern, commandFunction);
    }
  }

  /**
   * Register new command type
   *
   * @param regex the regex that detects the prefix of the string
   * @param clazz the class
   * @deprecated use {@link #register(Function, String...)} instead
   */
  @Deprecated
  public static void register(String regex, Class<? extends Command> clazz) {
    register(s -> {
      try {
        return clazz.getDeclaredConstructor(String.class)
            .newInstance(s);
      } catch (Exception e) {
        throw new RuntimeException("Invalid command class");
      }
    }, regex);
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
