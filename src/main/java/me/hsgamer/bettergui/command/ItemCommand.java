package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.ArrayList;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.TestCase;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class ItemCommand extends BukkitCommand {

  private TestCase<CommandSender> testCase = new TestCase<CommandSender>()
      .setPredicate(sender -> sender instanceof Player)
      .setSuccessConsumer(
          sender -> getInstance().getItemsConfig().createMenu((Player) sender))
      .setFailConsumer(sender -> CommonUtils.sendMessage(sender,
          getInstance().getMessageConfig().get(DefaultMessage.PLAYER_ONLY)));

  public ItemCommand() {
    super("items", "Open the inventory that contains all items from items.yml", "items",
        new ArrayList<>());
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    return testCase.setTestObject(commandSender).test();
  }
}
