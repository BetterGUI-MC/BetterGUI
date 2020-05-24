package me.hsgamer.bettergui.object.icon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.ParentIcon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class ListIcon extends Icon implements ParentIcon {

  private final List<Icon> icons = new ArrayList<>();
  private final Map<UUID, Integer> currentIndexMap = new HashMap<>();
  private boolean keepCurrentIndex = false;

  public ListIcon(String name, Menu<?> menu) {
    super(name, menu);
  }

  public ListIcon(Icon original) {
    super(original);
    if (original instanceof ListIcon) {
      this.icons.addAll(((ListIcon) original).icons);
      this.keepCurrentIndex = ((ListIcon) original).keepCurrentIndex;
    }
  }

  @Override
  public void setFromSection(ConfigurationSection section) {
    setChildFromSection(getMenu(), section);
    section.getKeys(false).forEach(key -> {
      if (key.equalsIgnoreCase("keep-current-index")) {
        keepCurrentIndex = section.getBoolean(key);
      }
    });
  }

  @Override
  public Optional<ClickableItem> createClickableItem(Player player) {
    for (int i = 0; i < icons.size(); i++) {
      Optional<ClickableItem> item = icons.get(i).createClickableItem(player);
      if (item.isPresent()) {
        currentIndexMap.put(player.getUniqueId(), i);
        return item;
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<ClickableItem> updateClickableItem(Player player) {
    if (keepCurrentIndex && currentIndexMap.containsKey(player.getUniqueId())) {
      return icons.get(currentIndexMap.get(player.getUniqueId())).updateClickableItem(player);
    }

    Optional<ClickableItem> item = Optional.empty();
    for (int i = 0; i < icons.size(); i++) {
      Icon icon = icons.get(i);
      item = icon.updateClickableItem(player);
      if (item.isPresent()) {
        int currentIndex = currentIndexMap.getOrDefault(player.getUniqueId(), -1);
        if (currentIndex != i) {
          currentIndexMap.put(player.getUniqueId(), i);
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

  @Override
  public List<Icon> getChild() {
    return icons;
  }
}
