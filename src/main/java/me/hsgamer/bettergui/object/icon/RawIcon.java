package me.hsgamer.bettergui.object.icon;

import java.util.Optional;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class RawIcon extends Icon {

  public RawIcon(String name, Menu<?> menu) {
    super(name, menu);
  }

  public RawIcon(Icon original) {
    super(original);
  }

  @Override
  public void setFromSection(ConfigurationSection section) {
    // Ignored
  }

  @Override
  public Optional<ClickableItem> createClickableItem(Player player) {
    return Optional.empty();
  }

  @Override
  public Optional<ClickableItem> updateClickableItem(Player player) {
    return Optional.empty();
  }
}
