package me.hsgamer.bettergui.command;

import io.github.projectunified.minelib.plugin.command.CommandComponent;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.manager.MenuCommandManager;
import me.hsgamer.hscore.common.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

public class MainCommand extends BukkitCommand {
  private final BetterGUI plugin;

  public MainCommand(BetterGUI plugin) {
    super(plugin.getName().toLowerCase(), "Show all available commands", "/" + plugin.getName().toLowerCase(), new ArrayList<>());
    this.plugin = plugin;
    setPermission(Permissions.HELP.getName());
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    if (!testPermission(commandSender)) {
      return false;
    }

    CommandComponent manager = plugin.get(CommandComponent.class);
    sendMessage(commandSender, "");
    sendMessage(commandSender, "&e&lAuthor: &f" + Arrays.toString(plugin.getDescription().getAuthors().toArray()));
    sendMessage(commandSender, "&e&lVersion: &f" + plugin.getDescription().getVersion());
    sendMessage(commandSender, "");
    sendMessage(commandSender, "&b&lCommand: ");
    for (Command command : manager.getRegistered().values()) {
      sendMessage(commandSender, "  &6" + command.getUsage());

      String description = command.getDescription();
      if (!Validate.isNullOrEmpty(description.trim())) {
        sendMessage(commandSender, "    &bDesc: &f" + description);
      }

      List<String> aliases = command.getAliases();
      if (!aliases.isEmpty()) {
        sendMessage(commandSender, "    &cAlias: " + Arrays.toString(aliases.toArray()));
      }
    }
    sendMessage(commandSender, "");


    MenuCommandManager menuCommandManager = plugin.get(MenuCommandManager.class);
    sendMessage(commandSender, "&b&lMenu Command: ");
    for (Command command : menuCommandManager.getRegisteredMenuCommand().values()) {
      sendMessage(commandSender, "  &6" + command.getUsage());
    }
    sendMessage(commandSender, "");
    return true;
  }
}
