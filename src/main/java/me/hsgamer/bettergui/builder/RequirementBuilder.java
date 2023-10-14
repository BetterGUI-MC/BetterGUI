package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.requirement.type.*;
import me.hsgamer.hscore.builder.MassBuilder;

import java.util.Optional;
import java.util.function.Function;

/**
 * The requirement builder
 */
public final class RequirementBuilder extends MassBuilder<RequirementBuilder.Input, Requirement> {
  public static final RequirementBuilder INSTANCE = new RequirementBuilder();

  /**
   * The instance of the requirement builder
   */
  private RequirementBuilder() {
    register(LevelRequirement::new, "level");
    register(PermissionRequirement::new, "permission");
    register(CooldownRequirement::new, "cooldown");
    register(VersionRequirement::new, "version");
    register(ConditionRequirement::new, "condition");
  }

  /**
   * Register a new requirement creator
   *
   * @param creator the creator
   * @param type    the type
   */
  public void register(Function<Input, Requirement> creator, String... type) {
    register(input -> {
      String requirement = input.type;
      for (String s : type) {
        if (requirement.equalsIgnoreCase(s)) {
          return Optional.of(creator.apply(input));
        }
      }
      return Optional.empty();
    });
  }

  /**
   * The input of the requirement builder
   */
  public static class Input {
    public final Menu menu;
    public final String type;
    public final String name;
    public final Object value;

    /**
     * Create a new input
     *
     * @param menu  the menu
     * @param type  the type of the requirement
     * @param name  the name of the requirement
     * @param value the value of the requirement
     */
    public Input(Menu menu, String type, String name, Object value) {
      this.menu = menu;
      this.type = type;
      this.name = name;
      this.value = value;
    }
  }
}
