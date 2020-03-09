package me.hsgamer.bettergui.object.property.icon.impl;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.object.IconVariable;
import me.hsgamer.bettergui.object.property.IconProperty;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
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
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(getValue().getValues(false));
    // Per Click Type
    for (ClickType clickType : ClickType.values()) {
      String subsection = clickType.name();
      if (keys.containsKey(subsection)) {
        List<IconRequirement<?, ?>> requirements = RequirementBuilder
            .loadRequirementsFromSection((ConfigurationSection) keys.get(subsection),
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
    // Default
    if (keys.containsKey("DEFAULT")) {
      setDefaultRequirements((ConfigurationSection) keys.get("DEFAULT"));
    }
    // Alternative Default
    setDefaultRequirements(getValue());
  }

  private void setDefaultRequirements(ConfigurationSection section) {
    List<IconRequirement<?, ?>> requirements = RequirementBuilder
        .loadRequirementsFromSection(section,
            getIcon());
    defaultRequirements.addAll(requirements);
    requirements.forEach(iconRequirement -> {
      if (iconRequirement instanceof IconVariable) {
        getIcon().registerVariable("default_" + ((IconVariable) iconRequirement).getIdentifier(),
            (IconVariable) iconRequirement);
      }
    });
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
