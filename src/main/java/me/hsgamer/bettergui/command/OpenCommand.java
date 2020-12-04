package me.hsgamer.bettergui.command;

import me.hsgamer.bettergui.Permissions;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.manager.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.hsgamer.bettergui.BetterGUI.getInstance;
import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

public final class OpenCommand extends BukkitCommand {

  private final MenuManager menuManager = getInstance().getMenuManager();

  public OpenCommand() {
    super("openmenu", "Open the specific menu", "/openmenu <menu_name> [player] [args...]", Collections.singletonList("om"));
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    if (!commandSender.hasPermission(Permissions.OPEN_MENU)) {
      sendMessage(commandSender, MessageConfig.NO_PERMISSION.getValue());
      return false;
    }
    if (strings.length <= 0) {
      sendMessage(commandSender, MessageConfig.MENU_REQUIRED.getValue());
      return false;
    }
    if (!menuManager.contains(strings[0])) {
      sendMessage(commandSender, MessageConfig.MENU_NOT_FOUND.getValue());
      return false;
    }

    if (strings.length == 1) {
      if (commandSender instanceof Player) {
        menuManager.openMenu(strings[0], (Player) commandSender, new String[0], false);
        return true;
      } else {
        sendMessage(commandSender, MessageConfig.PLAYER_ONLY.getValue());
        return false;
      }
    }

    Player player = Bukkit.getPlayer(strings[1]);
    if (player == null || !player.isOnline()) {
      sendMessage(commandSender, MessageConfig.PLAYER_NOT_FOUND.getValue());
      return false;
    }

    String[] args = strings.length > 2 ? Arrays.copyOfRange(strings, 2, strings.length) : new String[0];

    menuManager.openMenu(strings[0], player, args, true);
    return true;
  }

  @Override
  public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
    List<String> list = new ArrayList<>();
    if (args.length == 1) {
      list.addAll(getInstance().getMenuManager().getMenuNames());
    } else if (args.length == 2) {
      list.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
    }
    return list;
  }
}