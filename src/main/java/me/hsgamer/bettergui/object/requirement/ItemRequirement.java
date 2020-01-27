package me.hsgamer.bettergui.object.requirement;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.object.icon.DummyIcon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.bettergui.object.property.item.impl.Amount;
import me.hsgamer.bettergui.object.requirement.ItemRequirement.RequiredItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemRequirement extends IconRequirement<RequiredItem> {

  public ItemRequirement(Icon icon) {
    super(icon, true);
  }

  @Override
  public List<RequiredItem> getParsedValue(Player player) {
    List<RequiredItem> list = new ArrayList<>();
    Map<String, DummyIcon> icons = getInstance().getItemsConfig().getMenu().getIcons();
    for (String value : values) {
      String[] split = value.split(":", 2);
      if (icons.containsKey(split[0])) {
        DummyIcon icon = icons.get(split[0]);
        if (split.length == 2) {
          list.add(new RequiredItem(icon, Boolean.parseBoolean(split[1])));
        } else {
          list.add(new RequiredItem(icon, true));
        }
      } else {
        // TODO: Config, Send "Invalid required icons"
      }
    }
    return list;
  }

  // TODO: Config
  @Override
  public boolean check(Player player) {
    for (RequiredItem requiredItem : getParsedValue(player)) {
      DummyIcon dummyIcon = requiredItem.icon;
      int amountNeeded = dummyIcon.createClickableItem(player).get().getItem().getAmount();
      int amountFound = 0;
      for (ItemStack item : player.getInventory().getContents()) {
        if (checkItem(player, dummyIcon, item, requiredItem.oldCheck)) {
          amountFound += item.getAmount();
        }
      }
      if (amountFound < amountNeeded) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void take(Player player) {
    for (RequiredItem requiredItem : getParsedValue(player)) {
      DummyIcon dummyIcon = requiredItem.icon;
      ItemStack[] contents = player.getInventory().getContents();
      int amountNeeded = dummyIcon.createClickableItem(player).get().getItem().getAmount();
      for (int i = 0; i < contents.length; i++) {
        ItemStack item = contents[i];
        if (checkItem(player, dummyIcon, item, requiredItem.oldCheck)) {
          if (item.getAmount() > amountNeeded) {
            item.setAmount(item.getAmount() - amountNeeded);
            return;
          } else {
            amountNeeded -= item.getAmount();
            player.getInventory().setItem(i, XMaterial.AIR.parseItem());
          }
        }
        if (amountNeeded <= 0) {
          return;
        }
      }
    }
  }

  private boolean checkItem(Player player, DummyIcon dummyIcon, ItemStack item, boolean oldCheck) {
    if (oldCheck) {
      return item != null && item.isSimilar(dummyIcon.createClickableItem(player).get().getItem());
    } else {
      boolean notItemNeeded = true;
      for (ItemProperty<?, ?> property : dummyIcon.getItemProperties().values()) {
        if (property instanceof Amount) {
          continue;
        }
        if (!property.compareWithItemStack(player, item)) {
          notItemNeeded = false;
          break;
        }
      }
      return !notItemNeeded;
    }
  }

  static class RequiredItem {

    DummyIcon icon;
    boolean oldCheck;

    RequiredItem(DummyIcon icon, boolean oldCheck) {
      this.icon = icon;
      this.oldCheck = oldCheck;
    }
  }
}
