package me.hsgamer.bettergui.object.icon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.ParentIcon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class AnimatedIcon extends Icon implements ParentIcon {

  private List<Icon> icons = new ArrayList<>();
  private int currentIndex;
  private int update = 0;
  private int currentTime;
  private Icon currentIcon;

  public AnimatedIcon(String name, Menu menu) {
    super(name, menu);
  }

  public AnimatedIcon(Icon original) {
    super(original);
    if (original instanceof AnimatedIcon) {
      this.icons = ((AnimatedIcon) original).icons;
      this.update = ((AnimatedIcon) original).update;
    }
  }

  @Override
  public void setFromSection(ConfigurationSection section) {
    setChildFromSection(getMenu(), section);
    section.getKeys(false).forEach(key -> {
      if (key.equalsIgnoreCase("update")) {
        update = section.getInt(key);
      }
    });
  }

  @Override
  public Optional<ClickableItem> createClickableItem(Player player) {
    currentIndex = 0;
    currentIcon = icons.get(currentIndex);
    currentTime = update;
    return currentIcon.createClickableItem(player);
  }

  @Override
  public Optional<ClickableItem> updateClickableItem(Player player) {
    if (currentTime > 0) {
      currentTime--;
    } else {
      currentIcon = icons.get(getFrame());
      currentTime = update;
    }
    return currentIcon.updateClickableItem(player);
  }

  private int getFrame() {
    if (currentIndex < icons.size() - 1) {
      currentIndex++;
    } else {
      currentIndex = 0;
    }
    return currentIndex;
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
