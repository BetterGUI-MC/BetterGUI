package me.hsgamer.bettergui.object.requirement;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.object.icon.DummyIcon;
import me.hsgamer.bettergui.object.requirement.ItemRequirement.RequiredItem;
import org.bukkit.entity.Player;

// TODO: Finish up
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

  @Override
  public boolean check(Player player) {
    return false;
  }

  @Override
  public void take(Player player) {

  }

  class RequiredItem {

    private DummyIcon icon;
    private boolean oldCheck;

    protected RequiredItem(DummyIcon icon, boolean oldCheck) {
      this.icon = icon;
      this.oldCheck = oldCheck;
    }

    public DummyIcon getIcon() {
      return icon;
    }

    public boolean isOldCheck() {
      return oldCheck;
    }
  }
}
