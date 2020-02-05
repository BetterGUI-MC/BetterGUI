package me.hsgamer.bettergui.object.property.icon;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.object.IconVariable;
import me.hsgamer.bettergui.object.property.IconProperty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ClickRequirement extends IconProperty<ConfigurationSection> {

  private final Map<ClickType, List<IconRequirement<?, ?>>> requirementsPerClickType = new EnumMap<>(
      ClickType.class);
  private final List<IconRequirement<?, ?>> defaultRequirements = new ArrayList<>();

  public ClickRequirement(Icon icon) {
    super(icon);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    for (ClickType clickType : ClickType.values()) {
      String subsection = clickType.name();
      if (getValue().isConfigurationSection(subsection)) {
        List<IconRequirement<?, ?>> requirements = RequirementBuilder
            .loadRequirementsFromSection(getValue().getConfigurationSection(subsection),
                getIcon());
        requirementsPerClickType.put(clickType, requirements);
        requirements.forEach(iconRequirement -> {
          if (iconRequirement instanceof IconVariable) {
            getIcon().registerVariable(
                subsection.toLowerCase() + "_" + ((IconVariable) iconRequirement).getIdentifier(),
                (IconVariable) iconRequirement);
          }
        });
      }
    }
    if (getValue().isSet("DEFAULT")) {
      List<IconRequirement<?, ?>> requirements = RequirementBuilder
          .loadRequirementsFromSection(getValue().getConfigurationSection("DEFAULT"),
              getIcon());
      defaultRequirements.addAll(requirements);
      requirements.forEach(iconRequirement -> {
        if (iconRequirement instanceof IconVariable) {
          getIcon().registerVariable("default_" + ((IconVariable) iconRequirement).getIdentifier(),
              (IconVariable) iconRequirement);
        }
      });
    }
  }

  public boolean check(Player player, ClickType clickType) {
    for (IconRequirement<?, ?> requirement : requirementsPerClickType
        .getOrDefault(clickType, defaultRequirements)) {
      if (!requirement.check(player)) {
        return false;
      }
    }
    return true;
  }

  public void take(Player player, ClickType clickType) {
    for (IconRequirement<?, ?> requirement : requirementsPerClickType
        .getOrDefault(clickType, defaultRequirements)) {
      if (requirement.canTake()) {
        requirement.take(player);
      }
    }
  }
}
