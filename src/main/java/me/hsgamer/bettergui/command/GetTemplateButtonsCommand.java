package me.hsgamer.bettergui.command;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Arrays;

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

public class GetTemplateButtonsCommand extends BukkitCommand {
  public GetTemplateButtonsCommand() {
    super("gettemplatebuttons", "Get the registered template buttons", "/gettemplatebuttons", Arrays.asList("templates", "templatebuttons"));
    setPermission(Permissions.TEMPLATE_BUTTON.getName());
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    if (!testPermission(sender)) {
      return false;
    }
    sendMessage(sender, "&bRegistered Template Buttons:");
    BetterGUI.getInstance().getTemplateButtonConfig().getAllTemplateButtonNames().stream().sorted().forEach(prefix -> sendMessage(sender, "&f- &e" + prefix));
    return true;
  }
}
