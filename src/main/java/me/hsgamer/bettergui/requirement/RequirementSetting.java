package me.hsgamer.bettergui.requirement;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.*;

/**
 * The requirement setting used in Menus/Buttons/...
 */
public class RequirementSetting {
  private final Menu menu;
  private final String name;
  private final List<RequirementSet> requirementSets = new LinkedList<>();
  private final List<Action> actions = new LinkedList<>();
  private final CheckedRequirementSet checked = new CheckedRequirementSet();

  /**
   * Create a new requirement setting
   *
   * @param menu the menu
   * @param name the name
   */
  public RequirementSetting(Menu menu, String name) {
    this.menu = menu;
    this.name = name;
  }

  /**
   * Load settings from the section
   *
   * @param section the section
   */
  public void loadFromSection(ConfigurationSection section) {
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section.getValues(false));
    keys.forEach((key, value) -> {
      if (value instanceof ConfigurationSection) {
        requirementSets.add(RequirementBuilder.INSTANCE.getRequirementSet(menu, name + "_reqset_" + key, (ConfigurationSection) value));
      }
    });
    Optional.ofNullable(keys.get("fail-command")).ifPresent(o -> actions.addAll(ActionBuilder.INSTANCE.getActions(menu, o)));
  }

  /**
   * Check if the unique id meets all requirements
   *
   * @param uuid the unique id
   *
   * @return true if it does
   */
  public boolean check(UUID uuid) {
    if (requirementSets.isEmpty()) {
      return true;
    }
    for (RequirementSet requirement : requirementSets) {
      if (requirement.check(uuid)) {
        checked.put(uuid, requirement);
        return true;
      }
    }
    return false;
  }

  /**
   * Get checked requirement set
   *
   * @param uuid the the unique id
   *
   * @return the checked requirement set
   */
  public Optional<RequirementSet> getCheckedRequirement(UUID uuid) {
    return checked.get(uuid);
  }

  /**
   * Run the fail actions
   *
   * @param uuid the unique id
   */
  public void sendFailActions(UUID uuid) {
    TaskChain<?> taskChain = BetterGUI.newChain();
    actions.forEach(action -> action.addToTaskChain(uuid, taskChain));
    taskChain.execute();
  }

  /**
   * Get the list of requirement sets
   *
   * @return the list of requirement sets
   */
  public List<RequirementSet> getRequirementSets() {
    return requirementSets;
  }
}
