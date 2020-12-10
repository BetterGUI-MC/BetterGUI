package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.requirement.type.ConditionRequirement;
import me.hsgamer.bettergui.requirement.type.CooldownRequirement;
import me.hsgamer.bettergui.requirement.type.LevelRequirement;
import me.hsgamer.bettergui.requirement.type.PermissionRequirement;
import me.hsgamer.bettergui.requirement.RequirementSet;
import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The requirement builder
 */
public class RequirementBuilder extends Builder<String, Requirement> {

  /**
   * The instance of the requirement builder
   */
  public static final RequirementBuilder INSTANCE = new RequirementBuilder();

  private RequirementBuilder() {
    registerDefaultRequirements();
  }

  private void registerDefaultRequirements() {
    register(ConditionRequirement::new, "condition");
    register(LevelRequirement::new, "level");
    register(PermissionRequirement::new, "permission");
    register(CooldownRequirement::new, "cooldown");
  }

  /**
   * Build the requirement
   *
   * @param menu  the menu
   * @param type  the type of the requirement
   * @param name  the name of the requirement
   * @param value the value
   *
   * @return the requirement
   */
  public Optional<Requirement> getRequirement(Menu menu, String type, String name, Object value) {
    return build(type, name).map(requirement -> {
      requirement.setMenu(menu);
      requirement.setValue(value);
      return requirement;
    });
  }

  /**
   * Build the requirement set
   *
   * @param menu    the menu
   * @param name    the name of the set
   * @param section the section
   *
   * @return the requirement set
   */
  public RequirementSet getRequirementSet(Menu menu, String name, ConfigurationSection section) {
    List<Requirement> requirements = section.getMapValues(false).entrySet().stream().flatMap(entry -> {
      String type = entry.getKey();
      Object value = entry.getValue();
      return getRequirement(menu, type, name + "_" + type, value).map(Stream::of).orElse(Stream.empty());
    }).collect(Collectors.toList());

    RequirementSet requirementSet = new RequirementSet(name, menu, requirements);
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section.getValues(false));

    Optional.ofNullable(keys.get("success-commands")).ifPresent(o -> requirementSet.setSuccessActions(ActionBuilder.INSTANCE.getActions(menu, o)));
    Optional.ofNullable(keys.get("fail-commands")).ifPresent(o -> requirementSet.setFailActions(ActionBuilder.INSTANCE.getActions(menu, o)));

    return requirementSet;
  }
}
