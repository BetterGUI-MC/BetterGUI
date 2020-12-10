package me.hsgamer.bettergui.command;

import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Arrays;

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

public class GetVariablesCommand extends BukkitCommand {
  public GetVariablesCommand() {
    super("getvariables", "Get the registered variables", "/getvariables", Arrays.asList("variables", "placeholders", "getplaceholders"));
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    if (!sender.hasPermission(Permissions.VARIABLE)) {
      sendMessage(sender, MessageConfig.NO_PERMISSION.getValue());
      return false;
    }
    sendMessage(sender, "&bRegistered Variables:");
    VariableManager.getVariables().keySet().forEach(prefix -> sendMessage(sender, "&f- &e" + prefix));
    return true;
  }
}
