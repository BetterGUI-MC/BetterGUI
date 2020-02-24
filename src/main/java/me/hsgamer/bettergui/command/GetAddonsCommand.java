package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.Arrays;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.TestCase;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class GetAddonsCommand extends BukkitCommand {

  private TestCase<CommandSender> testCase = new TestCase<CommandSender>()
      .setPredicate(commandSender -> commandSender.hasPermission(Permissions.ADDONS))
      .setSuccessConsumer(
          commandSender -> CommonUtils.sendMessage(commandSender, "&b&lLoaded Addons: &c" + String
              .join(", ", getInstance().getAddonManager().getLoadedAddons())))
      .setFailConsumer(commandSender -> CommonUtils.sendMessage(commandSender,
          getInstance().getMessageConfig().get(DefaultMessage.NO_PERMISSION)));

  public GetAddonsCommand() {
    super("addons", "Get the loaded addons", "/addons",
        Arrays.asList("menuaddons", "getmenuaddons"));
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    return testCase.setTestObject(sender).test();
  }
}
