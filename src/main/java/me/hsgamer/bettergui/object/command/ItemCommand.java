package me.hsgamer.bettergui.object.command;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import co.aikar.taskchain.TaskChain;
import com.cryptomorin.xseries.XMaterial;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.icon.DummyIcon;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemCommand extends Command {

  public ItemCommand(String command) {
    super(command);
  }

  private ItemStack getItemStack(Player player, String input) {
    Map<String, DummyIcon> icons = getInstance().getItemsConfig().getMenu().getIcons();
    if (icons.containsKey(input)) {
      return icons.get(input).createClickableItem(player).get().getItem();
    } else {
      String[] split = input.split(",", 2);
      Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(split[0]);
      int amount = 1;
      if (xMaterial.isPresent()) {
        if (split.length >= 2) {
          try {
            amount = Integer.parseInt(split[1]);
          } catch (NumberFormatException e) {
            getInstance().getLogger().log(Level.WARNING, "Invalid amount on {0}", input);
            player.sendMessage(CommonUtils.colorize("&c&lInvalid amount of items. Will set to 1"));
          }
        }
        ItemStack itemStack = xMaterial.get().parseItem();
        if (itemStack != null) {
          itemStack.setAmount(amount);
          return itemStack;
        }
      }
    }
    return null;
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    String parsed = getParsedCommand(player);
    ItemStack itemStack = getItemStack(player, parsed);
    if (itemStack != null) {
      taskChain.sync(() -> player.getInventory().addItem(itemStack));
    } else {
      getInstance().getLogger().log(Level.WARNING, "Invalid item on {0}", parsed);
      player.sendMessage(
          CommonUtils.colorize("&cInvalid item. Inform the operators about this problem"));
    }
  }
}
