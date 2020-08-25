package me.hsgamer.bettergui.builder;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import me.hsgamer.bettergui.object.Requirement;
import me.hsgamer.bettergui.object.requirement.ConditionRequirement;
import me.hsgamer.bettergui.object.requirement.CooldownRequirement;
import me.hsgamer.bettergui.object.requirement.ExpLevelRequirement;
import me.hsgamer.bettergui.object.requirement.PermissionRequirement;
import me.hsgamer.bettergui.object.requirementset.RequirementSet;
import me.hsgamer.bettergui.object.variable.LocalVariableManager;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.configuration.ConfigurationSection;

public final class RequirementBuilder {

  private static final Map<String, Supplier<Requirement<?, ?>>> requirementTypes = new CaseInsensitiveStringMap<>();

  static {
    register(ConditionRequirement::new, "condition");
    register(ExpLevelRequirement::new, "level");
    register(PermissionRequirement::new, "permission");
    register(CooldownRequirement::new, "cooldown");
  }

  private RequirementBuilder() {

  }

  /**
   * Register new requirement type
   *
   * @param requirementSupplier the requirement supplier
   * @param type                the name of the type
   */
  public static void register(Supplier<Requirement<?, ?>> requirementSupplier, String... type) {
    for (String s : type) {
      if (s.toLowerCase().startsWith("not-")) {
        getInstance().getLogger()
            .warning(() -> "Invalid requirement type '" + s
                + "': Should not start with 'not-'. Ignored...");
        return;
      }
      requirementTypes.put(s, requirementSupplier);
    }
  }

  /**
   * Register new requirement type
   *
   * @param type  the name of the type
   * @param clazz the class
   * @deprecated use {@link #register(Supplier, String...)} instead
   */
  @Deprecated
  public static void register(String type, Class<? extends Requirement<?, ?>> clazz) {
    register(() -> {
      try {
        return clazz.getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        throw new RuntimeException("Invalid requirement class");
      }
    }, type);
  }

  public static Optional<Requirement<?, ?>> getRequirement(String type,
      LocalVariableManager<?> localVariableManager) {
    // Check Inverted mode
    boolean inverted = false;
    if (type.toLowerCase().startsWith("not-")) {
      type = type.substring(4);
      inverted = true;
    }

    if (!requirementTypes.containsKey(type)) {
      return Optional.empty();
    }

    Requirement<?, ?> requirement = requirementTypes.get(type).get();
    requirement.setVariableManager(localVariableManager);
    requirement.setInverted(inverted);
    return Optional.of(requirement);
  }

  public static List<Requirement<?, ?>> loadRequirementsFromSection(ConfigurationSection section,
      LocalVariableManager<?> localVariableManager) {
    List<Requirement<?, ?>> requirements = new ArrayList<>();
    section.getKeys(false).forEach(type -> {
      Optional<Requirement<?, ?>> rawRequirement = getRequirement(type, localVariableManager);
      if (!rawRequirement.isPresent()) {
        return;
      }
      Requirement<?, ?> requirement = rawRequirement.get();
      if (section.isConfigurationSection(type)) {
        Map<String, Object> keys = new CaseInsensitiveStringMap<>(
            section.getConfigurationSection(type).getValues(false));
        if (keys.containsKey(Settings.VALUE)) {
          requirement.setValue(keys.get(Settings.VALUE));
          if (keys.containsKey(Settings.TAKE)) {
            requirement.canTake((Boolean) keys.get(Settings.TAKE));
          }
        } else {
          getInstance().getLogger().warning(
              "The requirement \"" + type + "\" doesn't have VALUE");
        }
      } else {
        requirement.setValue(section.get(type));
      }
      requirements.add(requirement);
    });

    return requirements;
  }

  public static List<RequirementSet> getRequirementSet(ConfigurationSection section,
      LocalVariableManager<?> localVariableManager) {
    List<RequirementSet> list = new ArrayList<>();
    section.getKeys(false).forEach(key -> {
      if (section.isConfigurationSection(key)) {
        ConfigurationSection subsection = section.getConfigurationSection(key);
        List<Requirement<?, ?>> requirements = loadRequirementsFromSection(subsection,
            localVariableManager);
        if (!requirements.isEmpty()) {
          RequirementSet requirementSet = new RequirementSet(key, requirements);
          Map<String, Object> keys = new CaseInsensitiveStringMap<>(subsection.getValues(false));
          if (keys.containsKey(Settings.SUCCESS_COMMAND)) {
            requirementSet.setSuccessCommands(CommandBuilder.getCommands(localVariableManager,
                CommonUtils.createStringListFromObject(keys.get(Settings.SUCCESS_COMMAND), true)));
          }
          if (keys.containsKey(Settings.FAIL_COMMAND)) {
            requirementSet.setFailCommands(CommandBuilder.getCommands(localVariableManager,
                CommonUtils.createStringListFromObject(keys.get(Settings.FAIL_COMMAND), true)));
          }
          list.add(requirementSet);
        }
      }
    });
    return list;
  }

  private static class Settings {

    // Requirement settings
    static final String VALUE = "value";
    static final String TAKE = "take";

    // Set settings
    static final String SUCCESS_COMMAND = "success-command";
    static final String FAIL_COMMAND = "fail-command";
  }
}
