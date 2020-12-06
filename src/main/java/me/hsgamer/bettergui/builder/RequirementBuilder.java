package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.hscore.builder.Builder;

import java.util.Optional;

/**
 * The requirement builder
 */
public class RequirementBuilder extends Builder<String, Requirement> {

  /**
   * The instance of the requirement builder
   */
  public static final RequirementBuilder INSTANCE = new RequirementBuilder();

  private static final String NOT_PREFIX = "not-";

  private RequirementBuilder() {
    registerDefaultRequirements();
  }

  private void registerDefaultRequirements() {

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
    boolean inverted = type.toLowerCase().startsWith(NOT_PREFIX);
    if (inverted) {
      type = type.substring(NOT_PREFIX.length());
    }

    return build(type, name).map(requirement -> {
      requirement.setInverted(inverted);
      requirement.setMenu(menu);
      requirement.setValue(value);
      return requirement;
    });
  }
}
