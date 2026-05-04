package me.hsgamer.bettergui.builder;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.bettergui.api.element.MenuElement;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.requirement.type.*;
import me.hsgamer.hscore.builder.FunctionalMassBuilder;

/**
 * The requirement builder
 */
public final class RequirementBuilder extends FunctionalMassBuilder<RequirementBuilder.Input, Requirement> implements Loadable {
  public RequirementBuilder() {
  }

  @Override
  public void load() {
    register(LevelRequirement::new, "level");
    register(PermissionRequirement::new, "permission");
    register(CooldownRequirement::new, "cooldown");
    register(VersionRequirement::new, "version");
    register(ConditionRequirement::new, "condition");
  }

  @Override
  public void disable() {
    clear();
  }

  @Override
  protected String getType(Input input) {
    return input.type;
  }

  /**
   * The input of the requirement builder
   */
  public static class Input {
    public final MenuElement parent;
    public final String type;
    public final Object value;

    /**
     * Create a new input
     *
     * @param parent the parent element
     * @param type   the type of the requirement
     * @param value  the value of the requirement
     */
    public Input(MenuElement parent, String type, Object value) {
      this.parent = parent;
      this.type = type;
      this.value = value;
    }
  }
}
