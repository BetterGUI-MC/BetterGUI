package me.hsgamer.bettergui.object.property.icon;

import java.util.ArrayList;
import java.util.List;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.object.property.IconProperty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ViewRequirement extends IconProperty<ConfigurationSection> {

  private final List<IconRequirement<?,?>> requirements = new ArrayList<>();

  public ViewRequirement(Icon icon) {
    super(icon);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    requirements.addAll(RequirementBuilder.loadRequirementsFromSection(getValue(), getIcon()));
  }

  public boolean check(Player player) {
    for (IconRequirement<?,?> requirement : requirements) {
      if (!requirement.check(player)) {
        return false;
      }
    }
    return true;
  }

  public void take(Player player) {
    for (IconRequirement<?,?> requirement : requirements) {
      if (requirement.canTake()) {
        requirement.take(player);
      }
    }
  }
}
