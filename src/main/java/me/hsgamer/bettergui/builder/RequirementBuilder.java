package me.hsgamer.bettergui.builder;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconRequirement;
import me.hsgamer.bettergui.object.requirement.ConditionRequirement;
import me.hsgamer.bettergui.object.requirement.ExpLevelRequirement;
import me.hsgamer.bettergui.object.requirement.ItemRequirement;
import me.hsgamer.bettergui.object.requirement.PermissionRequirement;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class RequirementBuilder {

  private static final Map<String, Class<? extends IconRequirement<?,?>>> requirements = new CaseInsensitiveStringMap<>();

  static {
    register("condition", ConditionRequirement.class);
    register("level", ExpLevelRequirement.class);
    register("permission", PermissionRequirement.class);
    register("item", ItemRequirement.class);
  }

  private RequirementBuilder() {

  }

  /**
   * Register new requirement type
   *
   * @param type  the name of the type
   * @param clazz the class
   */
  public static void register(String type, Class<? extends IconRequirement<?,?>> clazz) {
    requirements.put(type, clazz);
  }

  /**
   * Check the integrity of the classes
   */
  public static void checkClass() {
    for (Class<? extends IconRequirement<?,?>> clazz : requirements.values()) {
      try {
        clazz.getDeclaredConstructor(Icon.class).newInstance(new Icon("", null) {
          @Override
          public void setFromSection(ConfigurationSection section) {
            // IGNORED
          }

          @Override
          public Optional<ClickableItem> createClickableItem(Player player) {
            return Optional.empty();
          }

          @Override
          public Optional<ClickableItem> updateClickableItem(Player player) {
            return Optional.empty();
          }
        });
      } catch (Exception ex) {
        getInstance().getLogger()
            .log(Level.WARNING, "There is an unknown error on " + clazz.getSimpleName()
                + ". The requirement will be ignored", ex);
      }
    }
  }

  public static Optional<IconRequirement<?,?>> getRequirement(String type, Icon icon) {
    if (requirements.containsKey(type)) {
      Class<? extends IconRequirement<?,?>> clazz = requirements.get(type);
      try {
        return Optional.of(clazz.getDeclaredConstructor(Icon.class).newInstance(icon));
      } catch (Exception e) {
        // IGNORED
      }
    }
    return Optional.empty();
  }

  public static List<IconRequirement<?,?>> loadRequirementsFromSection(ConfigurationSection section,
      Icon icon) {
    List<IconRequirement<?,?>> requirements = new ArrayList<>();
    section.getKeys(false).forEach(type -> {
      Optional<IconRequirement<?,?>> rawRequirement = getRequirement(type, icon);
      if (!rawRequirement.isPresent()) {
        return;
      }
      IconRequirement<?,?> requirement = rawRequirement.get();
      if (section.isConfigurationSection(type)) {
        Map<String, Object> keys = new CaseInsensitiveStringMap<>(section.getValues(false));
        if (keys.containsKey(Settings.VALUE)) {
          requirement.setValue(keys.get(Settings.VALUE));
          if (keys.containsKey(Settings.MESSAGE)) {
            requirement.setFailMessage(
                CommonUtils.colorize(String.valueOf(keys.get(Settings.MESSAGE))));
          }
          if (keys.containsKey(Settings.TAKE)) {
            requirement.canTake((Boolean) keys.get(Settings.TAKE));
          }
        } else {
          getInstance().getLogger().warning(
              "The requirement \"" + type + "\" in the icon \"" + icon.getName()
                  + "\" in the menu \"" + icon.getMenu().getName()
                  + "\" doesn't have VALUE");
          return;
        }
        requirements.add(requirement);
      } else if (section.isSet(type)) {
        requirement.setValue(section.get(type));
        requirements.add(requirement);
      }
    });

    return requirements;
  }

  private static class Settings {

    static final String VALUE = "type";
    static final String MESSAGE = "message";
    static final String TAKE = "take";
  }
}
