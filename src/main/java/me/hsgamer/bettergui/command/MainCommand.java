package me.hsgamer.bettergui.command;

import java.util.ArrayList;
import java.util.Arrays;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.manager.CommandManager;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.TestCase;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class MainCommand extends BukkitCommand {

  private final TestCase<CommandSender> testCase = new TestCase<CommandSender>()
      .setPredicate(sender -> sender.hasPermission(Permissions.HELP))
      .setSuccessConsumer(sender -> {
        CommandManager manager = BetterGUI.getInstance().getCommandManager();
        CommonUtils.sendMessage(sender, "");
        CommonUtils.sendMessage(sender, "&b&lCommand: ");
        for (BukkitCommand command : manager.getRegistered().values()) {
          CommonUtils.sendMessage(sender,
              "  &6" + command.getUsage() + ": &f" + command.getDescription());
          CommonUtils.sendMessage(sender,
              "    &cAlias: " + Arrays.toString(command.getAliases().toArray()));
        }
        CommonUtils.sendMessage(sender, "");
        CommonUtils.sendMessage(sender, "&b&lMenu Command: ");
        for (BukkitCommand command : manager.getRegisteredMenuCommand().values()) {
          CommonUtils.sendMessage(sender, "  &6" + command.getUsage());
        }
        CommonUtils.sendMessage(sender, "");
      })
      .setFailConsumer(sender -> CommonUtils.sendMessage(sender,
          BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.NO_PERMISSION)));

  public MainCommand(String name) {
    super(name, "Show all available commands", "/" + name, new ArrayList<>());
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    return testCase.setTestObject(commandSender).test();
  }
}
