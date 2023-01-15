package me.hsgamer.bettergui.command;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

public class GetVariablesCommand extends BukkitCommand {
  private final BetterGUI plugin;

  public GetVariablesCommand(BetterGUI plugin) {
    super("getvariables", "Get the registered variables", "/getvariables [menu_name]", Arrays.asList("variables", "placeholders", "getplaceholders"));
    this.plugin = plugin;
    setPermission(Permissions.VARIABLE.getName());
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    if (!testPermission(sender)) {
      return false;
    }
    List<String> variables = new ArrayList<>();
    if (args.length > 0) {
      Menu menu = plugin.getMenuManager().getMenu(args[0]);
      if (menu == null) {
        sendMessage(sender, plugin.getMessageConfig().menuNotFound);
        return false;
      }
      variables.addAll(menu.getVariableManager().getVariables().keySet());
    } else {
      variables.addAll(VariableManager.getVariables().keySet());
      for (String menuName : plugin.getMenuManager().getMenuNames()) {
        Menu menu = plugin.getMenuManager().getMenu(menuName);
        if (menu == null) continue;
        menu.getVariableManager().getVariables().keySet().forEach(variable -> variables.add("menu_" + menuName + "_" + variable));
      }
    }
    sendMessage(sender, "&bRegistered Variables:");
    variables.forEach(variable -> sendMessage(sender, "&f- &e" + variable));
    return true;
  }

  @Override
  public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
    List<String> list = new ArrayList<>();
    if (args.length == 1) {
      list.addAll(plugin.getMenuManager().getMenuNames());
    }
    return list;
  }
}
