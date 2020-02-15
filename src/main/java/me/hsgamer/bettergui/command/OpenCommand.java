package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.manager.MenuManager;
import me.hsgamer.bettergui.util.BukkitUtils;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.TestCase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class OpenCommand extends BukkitCommand {

  public OpenCommand() {
    super("openmenu", "Open the specific menu", "openmenu <menu_name> [player]",
        Collections.singletonList("om"));
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    MenuManager menuManager = getInstance().getMenuManager();
    return new TestCase<CommandSender>()
        .setTestObject(commandSender)
        .setPredicate(commandSender1 -> commandSender1.hasPermission(Permissions.OPEN_MENU))
        .setFailConsumer(commandSender1 -> CommonUtils.sendMessage(commandSender1,
            getInstance().getMessageConfig().get(DefaultMessage.NO_PERMISSION)))
        .setSuccessNextTestCase(new TestCase<String[]>()
            .setTestObject(strings)
            .setPredicate(strings1 -> strings1.length > 0)
            .setFailConsumer(strings1 -> CommonUtils.sendMessage(commandSender,
                getInstance().getMessageConfig().get(DefaultMessage.MENU_REQUIRED)))
            .setSuccessNextTestCase(new TestCase<String[]>()
                .setTestObject(strings)
                .setPredicate(strings2 -> menuManager.contains(strings2[0]))
                .setFailConsumer(strings2 -> CommonUtils.sendMessage(commandSender,
                    getInstance().getMessageConfig().get(DefaultMessage.MENU_NOT_FOUND)))
                .setSuccessNextTestCase(new TestCase<String[]>()
                    .setTestObject(strings)
                    .setPredicate(strings3 -> strings3.length > 1)
                    .setFailConsumer(strings3 -> {
                      if (commandSender instanceof Player) {
                        menuManager.openMenu(strings[0], (Player) commandSender);
                      } else {
                        CommonUtils.sendMessage(commandSender,
                            getInstance().getMessageConfig().get(DefaultMessage.PLAYER_ONLY));
                      }
                    })
                    .setSuccessConsumer(strings3 -> {
                      Player player = Bukkit.getPlayer(strings[1]);
                      if (player != null && player.isOnline()) {
                        menuManager.openMenu(strings[0], player);
                      } else {
                        CommonUtils.sendMessage(commandSender,
                            getInstance().getMessageConfig().get(DefaultMessage.PLAYER_NOT_FOUND));
                      }
                    })
                )
            )
        )
        .test();
  }

  @Override
  public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
    List<String> list = new ArrayList<>();
    if (args.length == 1) {
      list.addAll(getInstance().getMenuManager().getMenuNames());
    } else if (args.length == 2) {
      BukkitUtils.getOnlinePlayers().forEach(player -> list.add(player.getName()));
    }
    return list;
  }
}
