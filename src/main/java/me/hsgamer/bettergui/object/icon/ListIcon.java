package me.hsgamer.bettergui.object.icon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.hsgamer.bettergui.builder.IconBuilder;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.ParentIcon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

// TODO: IMPROVE The mechanic
public class ListIcon extends Icon implements ParentIcon {

  private List<Icon> icons = new ArrayList<>();
  private Map<UUID, Integer> indexPerPlayer = new HashMap<>();

  public ListIcon(String name, Menu menu) {
    super(name, menu);
  }

  @Override
  public void setFromSection(ConfigurationSection section) {
    icons.add(IconBuilder.getIcon(getMenu(), section, SimpleIcon.class));
  }

  @Override
  public ClickableItem createClickableItem(Player player) {
    for (int i = 1; i <= icons.size(); i++) {
      ClickableItem item = icons.get(i).createClickableItem(player);
      if (item != null) {
        indexPerPlayer.put(player.getUniqueId(), i);
        return item;
      }
    }
    return null;
  }

  @Override
  public ClickableItem updateClickableItem(Player player) {
    UUID uuid = player.getUniqueId();
    ClickableItem item = null;
    for (int i = 1; i <= icons.size(); i++) {
      Icon icon = icons.get(i);
      item = icon.updateClickableItem(player);
      if (item != null) {
        if (indexPerPlayer.containsKey(uuid) && indexPerPlayer.get(uuid) != i) {
          indexPerPlayer.put(uuid, i);
          return icon.createClickableItem(player);
        } else {
          break;
        }
      }
    }
    return item;
  }

  @Override
  public void addChild(Icon icon) {
    icons.add(icon);
  }
}
