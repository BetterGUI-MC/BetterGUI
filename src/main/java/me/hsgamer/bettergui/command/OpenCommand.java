package me.hsgamer.bettergui.command;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.Permissions;
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

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

public final class OpenCommand extends BukkitCommand {
  private final BetterGUI plugin;

  public OpenCommand(BetterGUI plugin) {
    super("openmenu", "Open the specific menu", "/openmenu <menu_name> [player] [args...]", Collections.singletonList("om"));
    setPermission(Permissions.OPEN_MENU.getName());
    this.plugin = plugin;
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] strings) {
    if (!testPermission(commandSender)) {
      return false;
    }
    if (strings.length == 0) {
      sendMessage(commandSender, plugin.getMessageConfig().getMenuRequired());
      return false;
    }
    if (!plugin.getMenuManager().contains(strings[0])) {
      sendMessage(commandSender, plugin.getMessageConfig().getMenuNotFound());
      return false;
    }

    Player player;
    String menuName = strings[0];
    String[] args = new String[0];

    if (strings.length > 1) {
      player = Bukkit.getPlayer(strings[1]);
      if (player == null || !player.isOnline()) {
        sendMessage(commandSender, plugin.getMessageConfig().getPlayerNotFound());
        return false;
      }
      args = Arrays.copyOfRange(strings, 2, strings.length);
    } else {
      if (commandSender instanceof Player) {
        player = (Player) commandSender;
      } else {
        sendMessage(commandSender, plugin.getMessageConfig().getPlayerOnly());
        return false;
      }
    }
    plugin.getMenuManager().openMenu(menuName, player, args, player.hasPermission(Permissions.OPEN_MENU_BYPASS));
    return true;
  }

  @Override
  public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
    List<String> list = new ArrayList<>();
    if (args.length == 1) {
      list.addAll(plugin.getMenuManager().getMenuNames());
    } else if (args.length == 2) {
      list.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
    } else if (args.length > 2 && plugin.getMenuManager().contains(args[0])) {
      Player player = Bukkit.getPlayer(args[1]);
      if (player != null && player.isOnline()) {
        list.addAll(plugin.getMenuManager().tabCompleteMenu(args[0], player, Arrays.copyOfRange(args, 2, args.length)));
      }
    }
    return list;
  }
}
