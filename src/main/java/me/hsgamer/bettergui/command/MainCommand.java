package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.util.CommonUtils.sendMessage;

import java.util.ArrayList;
import java.util.Arrays;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.manager.CommandManager;
import me.hsgamer.bettergui.util.TestCase;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class MainCommand extends BukkitCommand {

  private final TestCase<CommandSender> testCase = new TestCase<CommandSender>()
      .setPredicate(sender -> sender.hasPermission(Permissions.HELP))
      .setSuccessConsumer(sender -> {
        CommandManager manager = BetterGUI.getInstance().getCommandManager();
        sendMessage(sender, "");
        sendMessage(sender, "&e&lAuthor: &f" + Arrays
            .toString(BetterGUI.getInstance().getDescription().getAuthors().toArray()));
        sendMessage(sender, "&e&lVersion: &f" + BetterGUI.getInstance().getDescription().getVersion());
        sendMessage(sender, "&9&lDiscord: &fhttps://discord.gg/8mJJMqH");
        sendMessage(sender, "");
        sendMessage(sender, "&b&lCommand: ");
        for (BukkitCommand command : manager.getRegistered().values()) {
          sendMessage(sender,
              "  &6" + command.getUsage() + ": &f" + command.getDescription());
          sendMessage(sender,
              "    &cAlias: " + Arrays.toString(command.getAliases().toArray()));
        }
        sendMessage(sender, "");
        sendMessage(sender, "&b&lMenu Command: ");
        for (BukkitCommand command : manager.getRegisteredMenuCommand().values()) {
          sendMessage(sender, "  &6" + command.getUsage());
        }
        sendMessage(sender, "");
      })
      .setFailConsumer(sender -> sendMessage(sender,
          BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.NO_PERMISSION)));

  public MainCommand(String name) {
    super(name, "Show all available commands", "/" + name, new ArrayList<>());
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    return testCase.setTestObject(commandSender).test();
  }
}
