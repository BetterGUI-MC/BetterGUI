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

public class AnimatedIcon extends Icon implements ParentIcon {

  private List<Icon> icons = new ArrayList<>();
  private Map<UUID, Integer> currentFramePerPlayer = new HashMap<>();

  public AnimatedIcon(String name, Menu menu) {
    super(name, menu);
  }

  @Override
  public void setFromSection(ConfigurationSection section) {
    icons.add(IconBuilder.getIcon(getMenu(), section, SimpleIcon.class));
  }

  @Override
  public ClickableItem createClickableItem(Player player) {
    return icons.get(getFrame(player)).createClickableItem(player);
  }

  @Override
  public ClickableItem updateClickableItem(Player player) {
    return icons.get(getFrame(player)).updateClickableItem(player);
  }

  private int getFrame(Player player) {
    UUID uuid = player.getUniqueId();
    if (!currentFramePerPlayer.containsKey(uuid) || currentFramePerPlayer.get(uuid) >= icons
        .size()) {
      currentFramePerPlayer.put(player.getUniqueId(), 1);
    }
    int frame = currentFramePerPlayer.get(uuid);
    currentFramePerPlayer.put(uuid, frame + 1);
    return frame;
  }

  @Override
  public void addChild(Icon icon) {
    icons.add(icon);
  }
}
