package me.hsgamer.bettergui.object.requirement;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.hsgamer.bettergui.config.impl.MainConfig.DefaultConfig;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.object.icon.DummyIcon;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.bettergui.object.property.item.impl.Amount;
import me.hsgamer.bettergui.object.requirement.ItemRequirement.RequiredItem;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.ItemUtils;
import me.hsgamer.bettergui.util.VersionUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemRequirement extends IconRequirement<List<String>, List<RequiredItem>> {

  public ItemRequirement(Icon icon) {
    super(icon, true);
  }

  @Override
  public List<RequiredItem> getParsedValue(Player player) {
    List<RequiredItem> list = new ArrayList<>();
    Map<String, DummyIcon> icons = getInstance().getItemsConfig().getMenu().getIcons();
    for (String s : value) {
      String[] split = s.split(":", 2);
      String rawIcon = split[0].trim();
      if (icons.containsKey(rawIcon)) {
        DummyIcon icon = icons.get(rawIcon);
        if (split.length == 2) {
          list.add(new RequiredItem(icon, Boolean.parseBoolean(split[1].trim())));
        } else {
          list.add(new RequiredItem(icon, true));
        }
      } else {
        CommonUtils.sendMessage(player,
            getInstance().getMessageConfig().get(DefaultMessage.INVALID_ITEM));
      }
    }
    return list;
  }

  @Override
  public boolean check(Player player) {
    for (RequiredItem requiredItem : getParsedValue(player)) {
      DummyIcon dummyIcon = requiredItem.icon;
      ItemStack itemStack = dummyIcon.createClickableItem(player).get().getItem();
      int amountNeeded = itemStack.getAmount();
      int amountFound = 0;
      for (ItemStack item : player.getInventory().getContents()) {
        if (checkItem(player, dummyIcon, item, requiredItem.oldCheck)) {
          amountFound += item.getAmount();
        }
      }
      if (amountFound < amountNeeded) {
        String message = CommonUtils.colorize(
            failMessage != null ? failMessage
                : getInstance().getMessageConfig().get(DefaultMessage.NO_REQUIRED_ITEM));
        if (!message.isEmpty()) {
          message = message
              .replace("{item}",
                  (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName())
                      ? itemStack.getItemMeta().getDisplayName()
                      : XMaterial.matchXMaterial(itemStack).name())
              .replace("{amount}", Integer.toString(itemStack.getAmount()));
          if (VersionUtils.isSpigot() && (boolean) getInstance().getMainConfig()
              .get(DefaultConfig.USE_HOVER_EVENT)) {
            String itemJson = ItemUtils.convertItemStackToJson(itemStack);

            BaseComponent[] hoverEventComponents = new BaseComponent[]{
                new TextComponent(itemJson)};

            HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

            TextComponent component = new TextComponent(message);
            component.setHoverEvent(event);

            player.spigot().sendMessage(component);
          } else {
            player.sendMessage(message);
          }
        }
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
      for (ItemProperty<?, ?> property : dummyIcon.getItemProperties().values()) {
        if (property instanceof Amount) {
          continue;
        }
        if (!property.compareWithItemStack(player, item)) {
          return false;
        }
      }
      return true;
    }
  }

  static class RequiredItem {

    final DummyIcon icon;
    final boolean oldCheck;

    RequiredItem(DummyIcon icon, boolean oldCheck) {
      this.icon = icon;
      this.oldCheck = oldCheck;
    }
  }
}
