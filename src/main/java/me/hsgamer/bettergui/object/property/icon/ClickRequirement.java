package me.hsgamer.bettergui.object.property.icon;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.object.property.IconProperty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ClickRequirement extends IconProperty<ConfigurationSection> {

  private final Map<ClickType, List<IconRequirement<?>>> requirementsPerClickType = new EnumMap<>(
      ClickType.class);
  private final List<IconRequirement<?>> defaultRequirements = new ArrayList<>();

  public ClickRequirement(Icon icon) {
    super(icon);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    for (ClickType clickType : ClickType.values()) {
      String subsection = clickType.name();
      if (getValue().isConfigurationSection(subsection)) {
        requirementsPerClickType.put(clickType, RequirementBuilder
            .loadRequirementsFromSection(getValue().getConfigurationSection(subsection),
                getIcon()));
      }
    }
    defaultRequirements.addAll(RequirementBuilder
        .loadRequirementsFromSection(getValue().getConfigurationSection("DEFAULT"), getIcon()));
  }

  public boolean check(Player player, ClickType clickType) {
    for (IconRequirement<?> requirement : requirementsPerClickType
        .getOrDefault(clickType, defaultRequirements)) {
      if (!requirement.check(player)) {
        return false;
      }
    }
    return true;
  }

  public void take(Player player, ClickType clickType) {
    for (IconRequirement<?> requirement : requirementsPerClickType
        .getOrDefault(clickType, defaultRequirements)) {
      if (requirement.canTake()) {
        requirement.take(player);
      }
    }
  }
}
