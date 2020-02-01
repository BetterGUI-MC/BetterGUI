package me.hsgamer.bettergui.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.command.BackCommand;
import me.hsgamer.bettergui.object.command.BroadcastCommand;
import me.hsgamer.bettergui.object.command.ConditionCommand;
import me.hsgamer.bettergui.object.command.ConsoleCommand;
import me.hsgamer.bettergui.object.command.DelayCommand;
import me.hsgamer.bettergui.object.command.ItemCommand;
import me.hsgamer.bettergui.object.command.OpCommand;
import me.hsgamer.bettergui.object.command.OpenMenuCommand;
import me.hsgamer.bettergui.object.command.PlayerCommand;
import me.hsgamer.bettergui.object.command.TellCommand;

public class CommandBuilder {

  private static final Map<Pattern, Class<? extends Command>> commands = new HashMap<>();

  static {
    register("console:", ConsoleCommand.class);
    register("op:", OpCommand.class);
    register("player:", PlayerCommand.class);
    register("delay:", DelayCommand.class);
    register("condition:", ConditionCommand.class);
    register("give:", ItemCommand.class);
    register("open:", OpenMenuCommand.class);
    register("back", BackCommand.class);
    register("tell:", TellCommand.class);
    register("broadcast:", BroadcastCommand.class);
  }

  private CommandBuilder() {

  }

  public static void register(String regex, Class<? extends Command> clazz) {
    Pattern pattern = Pattern.compile("^(?i)" + regex);
    commands.put(pattern, clazz);
  }

  public static void checkClass() {
    for (Class<? extends Command> clazz : commands.values()) {
      try {
        clazz.getDeclaredConstructor(String.class).newInstance("");
      } catch (Exception ex) {
        BetterGUI.getInstance().getLogger()
            .log(Level.WARNING, "There is an unknown error on " + clazz.getSimpleName()
                + ". The command will be ignored", ex);
      }
    }
  }

  public static List<Command> getCommands(Icon icon, String input) {
    return getCommands(icon, Arrays.asList(input.split(";")));
  }

  public static List<Command> getCommands(Icon icon, List<String> input) {
    List<Command> list = new ArrayList<>();
    input.forEach(string -> list.add(getCommand(icon, string)));
    return list;
  }

  public static Command getCommand(Icon icon, String input) {
    for (Entry<Pattern, Class<? extends Command>> entry : commands.entrySet()) {
      Matcher matcher = entry.getKey().matcher(input);
      if (matcher.find()) {
        String cleanCommand = matcher.replaceFirst("").trim();

        try {
          Command command = entry.getValue().getDeclaredConstructor(String.class)
              .newInstance(cleanCommand);
          if (icon != null) {
            command.setIcon(icon);
          }
          return command;
        } catch (Exception e) {
          // Checked at startup
        }
      }
    }

    Command command = new PlayerCommand(input);
    if (icon != null) {
      command.setIcon(icon);
    }
    return command;
  }
}
