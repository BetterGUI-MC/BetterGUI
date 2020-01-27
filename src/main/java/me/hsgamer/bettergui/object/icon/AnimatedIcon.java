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

public class AnimatedIcon extends Icon implements ParentIcon {

  private List<Icon> icons = new ArrayList<>();
  private int currentIndex;

  public AnimatedIcon(String name, Menu menu) {
    super(name, menu);
  }

  public AnimatedIcon(Icon original) {
    super(original);
    if (original instanceof AnimatedIcon) {
      this.icons = ((AnimatedIcon) original).icons;
    }
  }

  @Override
  public void setFromSection(ConfigurationSection section) {
    setChildFromSection(getMenu(), section);
  }

  @Override
  public Optional<ClickableItem> createClickableItem(Player player) {
    currentIndex = 0;
    return icons.get(currentIndex).createClickableItem(player);
  }

  @Override
  public Optional<ClickableItem> updateClickableItem(Player player) {
    return icons.get(getFrame()).updateClickableItem(player);
  }

  private int getFrame() {
    if (currentIndex >= icons.size()) {
      currentIndex = 0;
    } else {
      currentIndex++;
    }
    return currentIndex;
  }

  @Override
  public void addChild(Icon icon) {
    icons.add(icon);
  }
}
