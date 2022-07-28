package me.hsgamer.bettergui.command;

import me.hsgamer.bettergui.Permissions;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Arrays;

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

public class GetVariablesCommand extends BukkitCommand {
  public GetVariablesCommand() {
    super("getvariables", "Get the registered variables", "/getvariables", Arrays.asList("variables", "placeholders", "getplaceholders"));
    setPermission(Permissions.VARIABLE.getName());
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    if (!testPermission(sender)) {
      return false;
    }
    sendMessage(sender, "&bRegistered Variables:");
    VariableManager.getVariables().keySet().stream().sorted().forEach(prefix -> sendMessage(sender, "&f- &e" + prefix));
    return true;
  }
}
