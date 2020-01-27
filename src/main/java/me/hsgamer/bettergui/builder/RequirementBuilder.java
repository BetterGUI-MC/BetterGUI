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

  private static final Map<String, Class<? extends IconRequirement<?>>> requirements = new CaseInsensitiveStringMap<>();

  static {
    register("condition", ConditionRequirement.class);
    register("level", ExpLevelRequirement.class);
    register("permission", PermissionRequirement.class);
    register("item", ItemRequirement.class);
  }

  public static void register(String type, Class<? extends IconRequirement<?>> clazz) {
    requirements.put(type, clazz);
  }

  public static void checkClass() {
    for (Class<? extends IconRequirement<?>> clazz : requirements.values()) {
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

  public static IconRequirement<?> getRequirement(String type, Icon icon) {
    if (requirements.containsKey(type)) {
      Class<? extends IconRequirement<?>> clazz = requirements.get(type);
      try {
        return clazz.getDeclaredConstructor(Icon.class).newInstance(icon);
      } catch (Exception e) {
        // IGNORED
      }
    }
    return null;
  }

  public static List<IconRequirement<?>> loadRequirementsFromSection(ConfigurationSection section,
      Icon icon) {
    List<IconRequirement<?>> requirements = new ArrayList<>();

    section.getKeys(false).forEach(type -> {
      IconRequirement<?> requirement = getRequirement(type, icon);
      if (requirement == null) {
        return;
      }
      if (section.isConfigurationSection(type)) {
        if (section.isSet(type + Settings.VALUE)) {
          if (section.isList(type + Settings.VALUE)) {
            requirement.setValues(section.getStringList(type + Settings.VALUE));
          } else {
            requirement.setValues(section.getString(type + Settings.VALUE));
          }
          requirement.setFailMessage(
              CommonUtils.colorize(section.getString(type + Settings.MESSAGE)));
          requirement.canTake(section.getBoolean(type + Settings.TAKE, true));
        } else {
          getInstance().getLogger().warning(
              "The requirement \"" + type + "\" in the icon \"" + icon.getName()
                  + "\" in the menu \"" + icon.getMenu().getName()
                  + "\" doesn't have VALUE");
          return;
        }
        requirements.add(requirement);
      } else if (section.isSet(type)) {
        if (section.isList(type)) {
          requirement.setValues(section.getStringList(type));
        } else {
          requirement.setValues(section.getString(type));
        }
        requirements.add(requirement);
      }
    });

    return requirements;
  }

  private static class Settings {

    static final String VALUE = ".VALUE";
    static final String MESSAGE = ".MESSAGE";
    static final String TAKE = ".TAKE";
  }
}
