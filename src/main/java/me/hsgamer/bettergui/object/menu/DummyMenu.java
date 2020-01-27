package me.hsgamer.bettergui.object.menu;

import fr.mrmicky.fastinv.FastInv;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.IconBuilder;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.icon.DummyIcon;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class DummyMenu extends Menu {

  private final Map<String, DummyIcon> icons = new HashMap<>();

  public DummyMenu(String name) {
    super(name);
  }

  @Override
  public void setFromFile(FileConfiguration file) {
    for (String key : file.getKeys(false)) {
      if (!icons.containsKey(key)) {
        icons.put(key,
            IconBuilder.getIcon(this, file.getConfigurationSection(key), DummyIcon.class));
      } else {
        BetterGUI.getInstance().getLogger().log(Level.WARNING, "Duplicated icon {0}", key);
      }
    }
  }

  @Override
  public void createInventory(Player player) {
    FastInv inv = new FastInv(54, CommonUtils.colorize("&f&lItems"));
    icons.values()
        .forEach(dummyIcon -> dummyIcon.createClickableItem(player)
            .ifPresent(item -> inv.addItem(item.getItem())));
    inv.open(player);
  }

  public Map<String, DummyIcon> getIcons() {
    return icons;
  }
}
