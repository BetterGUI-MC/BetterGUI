package me.hsgamer.bettergui.command;

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.manager.CommandManager;
import me.hsgamer.hscore.common.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public final class MainCommand extends BukkitCommand {

  public MainCommand(String name) {
    super(name, "Show all available commands", "/" + name, new ArrayList<>());
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    if (!commandSender.hasPermission(Permissions.HELP)) {
      sendMessage(commandSender, MessageConfig.NO_PERMISSION.getValue());
      return false;
    }

    CommandManager manager = BetterGUI.getInstance().getCommandManager();
    sendMessage(commandSender, "");
    sendMessage(commandSender, "&e&lAuthor: &f" + Arrays
        .toString(BetterGUI.getInstance().getDescription().getAuthors().toArray()));
    sendMessage(commandSender,
        "&e&lVersion: &f" + BetterGUI.getInstance().getDescription().getVersion());
    sendMessage(commandSender, "&9&lDiscord: &fhttps://discord.gg/9m4GdFD");
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
    sendMessage(commandSender, "&b&lMenu Command: ");
    for (Command command : manager.getRegisteredMenuCommand().values()) {
      sendMessage(commandSender, "  &6" + command.getUsage());
    }
    sendMessage(commandSender, "");
    return true;
  }
}
