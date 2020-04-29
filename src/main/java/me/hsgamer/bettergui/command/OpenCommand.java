package me.hsgamer.bettergui.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;
import static me.hsgamer.bettergui.util.CommonUtils.sendMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.manager.MenuManager;
import me.hsgamer.bettergui.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public final class OpenCommand extends BukkitCommand {

  private final MenuManager menuManager = getInstance().getMenuManager();

  public OpenCommand() {
    super("openmenu", "Open the specific menu",
        "/openmenu <menu_name> [<player_name>/me] [args...]",
        Collections.singletonList("om"));
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    if (!commandSender.hasPermission(Permissions.OPEN_MENU)) {
      sendMessage(commandSender,
          getInstance().getMessageConfig().get(DefaultMessage.NO_PERMISSION));
      return false;
    }
    if (strings.length <= 0) {
      sendMessage(commandSender,
          getInstance().getMessageConfig().get(DefaultMessage.MENU_REQUIRED));
      return false;
    }
    if (!menuManager.contains(strings[0])) {
      sendMessage(commandSender,
          getInstance().getMessageConfig().get(DefaultMessage.MENU_NOT_FOUND));
      return false;
    }

    if (strings.length == 1) {
      if (commandSender instanceof Player) {
        menuManager
            .openMenu(strings[0], (Player) commandSender, new String[0], false);
        return true;
      } else {
        sendMessage(commandSender,
            getInstance().getMessageConfig().get(DefaultMessage.PLAYER_ONLY));
        return false;
      }
    }

    Player player = Bukkit.getPlayer(strings[1]);
    if (player == null || !player.isOnline()) {
      sendMessage(commandSender,
          getInstance().getMessageConfig().get(DefaultMessage.PLAYER_NOT_FOUND));
      return false;
    }

    String[] args =
        strings.length > 2 ? Arrays.copyOfRange(strings, 2, strings.length) : new String[0];

    menuManager.openMenu(strings[0], player, args, true);
    return true;
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
