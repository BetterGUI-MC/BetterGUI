package me.hsgamer.bettergui.listener;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AlternativeCommandListener implements Listener {
  private static final Pattern SPACE_PATTERN = Pattern.compile("\\s");

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCommand(PlayerCommandPreprocessEvent event) {
    if (event.isCancelled()) {
      return;
    }

    List<String> ignoredCommands = MainConfig.ALTERNATIVE_COMMAND_MANAGER_IGNORED_COMMANDS.getValue();
    boolean caseInsensitive = MainConfig.ALTERNATIVE_COMMAND_MANAGER_CASE_INSENSITIVE.getValue();

    String rawCommand = event.getMessage().substring(1);
    if (ignoredCommands.stream().anyMatch(s -> caseInsensitive ? s.equalsIgnoreCase(rawCommand) : s.equals(rawCommand))) {
      return;
    }

    String[] split = SPACE_PATTERN.split(rawCommand);
    String command = split[0];
    String[] args = new String[0];
    if (split.length > 1) {
      args = Arrays.copyOfRange(split, 1, split.length);
    }

    Map<String, Command> menuCommand = BetterGUI.getInstance().getMenuCommandManager().getRegisteredMenuCommand();
    if (caseInsensitive) {
      menuCommand = new CaseInsensitiveStringMap<>(menuCommand);
    }

    if (menuCommand.containsKey(command)) {
      event.setCancelled(true);
      menuCommand.get(command).execute(event.getPlayer(), command, args);
    }
  }
}
