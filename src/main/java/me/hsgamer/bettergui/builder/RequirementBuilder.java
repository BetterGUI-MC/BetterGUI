package me.hsgamer.bettergui.builder;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.object.RequirementSet;
import me.hsgamer.bettergui.object.icon.RawIcon;
import me.hsgamer.bettergui.object.requirement.ConditionRequirement;
import me.hsgamer.bettergui.object.requirement.ExpLevelRequirement;
import me.hsgamer.bettergui.object.requirement.PermissionRequirement;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.TestCase;
import org.bukkit.configuration.ConfigurationSection;

public class RequirementBuilder {

  private static final Map<String, Class<? extends IconRequirement<?, ?>>> requirements = new CaseInsensitiveStringMap<>();

  static {
    register("condition", ConditionRequirement.class);
    register("level", ExpLevelRequirement.class);
    register("permission", PermissionRequirement.class);
  }

  private RequirementBuilder() {

  }

  /**
   * Register new requirement type
   *
   * @param type  the name of the type
   * @param clazz the class
   */
  public static void register(String type, Class<? extends IconRequirement<?, ?>> clazz) {
    requirements.put(type, clazz);
  }

  /**
   * Check the integrity of the classes
   */
  public static void checkClass() {
    for (Class<? extends IconRequirement<?, ?>> clazz : requirements.values()) {
      try {
        clazz.getDeclaredConstructor(Icon.class).newInstance(new RawIcon("", null));
      } catch (Exception ex) {
        getInstance().getLogger()
            .log(Level.WARNING, "There is an unknown error on " + clazz.getSimpleName()
                + ". The requirement will be ignored", ex);
      }
    }
  }

  public static Optional<IconRequirement<?, ?>> getRequirement(String type, Icon icon) {
    if (requirements.containsKey(type)) {
      Class<? extends IconRequirement<?, ?>> clazz = requirements.get(type);
      try {
        return Optional.of(clazz.getDeclaredConstructor(Icon.class).newInstance(icon));
      } catch (Exception e) {
        // IGNORED
      }
    }
    return Optional.empty();
  }

  public static List<IconRequirement<?, ?>> loadRequirementsFromSection(
      ConfigurationSection section,
      Icon icon) {
    List<IconRequirement<?, ?>> requirements = new ArrayList<>();
    section.getKeys(false).forEach(type -> {
      Optional<IconRequirement<?, ?>> rawRequirement = getRequirement(type, icon);
      if (!rawRequirement.isPresent()) {
        return;
      }
      IconRequirement<?, ?> requirement = rawRequirement.get();
      TestCase.create(type)
          .setPredicate(section::isConfigurationSection)
          .setSuccessConsumer(s -> {
            Map<String, Object> keys = new CaseInsensitiveStringMap<>(
                section.getConfigurationSection(s).getValues(false));
            if (keys.containsKey(Settings.VALUE)) {
              requirement.setValue(keys.get(Settings.VALUE));
              if (keys.containsKey(Settings.TAKE)) {
                requirement.canTake((Boolean) keys.get(Settings.TAKE));
              }
            } else {
              getInstance().getLogger().warning(
                  "The requirement \"" + s + "\" in the icon \"" + icon.getName()
                      + "\" in the menu \"" + icon.getMenu().getName()
                      + "\" doesn't have VALUE");
            }
          })
          .setFailConsumer(s -> requirement.setValue(section.get(s)))
          .test();
      requirements.add(requirement);
    });

    return requirements;
  }

  public static List<RequirementSet> getRequirementSet(ConfigurationSection section, Icon icon) {
    List<RequirementSet> list = new ArrayList<>();
    section.getKeys(false).forEach(key -> {
      ConfigurationSection subsection = section.getConfigurationSection(key);
      List<IconRequirement<?, ?>> iconRequirements = loadRequirementsFromSection(subsection, icon);
      if (iconRequirements.isEmpty()) {
        getInstance().getLogger().fine(() ->
            "The requirement set \"" + key + "\" in the icon \"" + icon.getName()
                + "\" in the menu \"" + icon.getMenu().getName()
                + "\" doesn't have any requirements");
      } else {
        RequirementSet requirementSet = new RequirementSet(key, iconRequirements);
        Map<String, Object> keys = new CaseInsensitiveStringMap<>(subsection.getValues(false));
        if (keys.containsKey(Settings.SUCCESS_COMMAND)) {
          requirementSet.setCommand(CommandBuilder.getCommands(icon,
              CommonUtils.createStringListFromObject(keys.get(Settings.SUCCESS_COMMAND), true)));
        }
        list.add(requirementSet);
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
  }
}
