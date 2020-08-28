package me.hsgamer.bettergui.object;

import co.aikar.taskchain.TaskChain;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.CommandBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.object.requirementset.CheckedRequirementSet;
import me.hsgamer.bettergui.object.requirementset.RequirementSet;
import me.hsgamer.hscore.common.CommonUtils;
import me.hsgamer.hscore.map.CaseInsensitiveStringMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * The requirement set for the menu
 */
public class GlobalRequirement {

  private final List<RequirementSet> requirements = new ArrayList<>();
  private final List<Command> commands = new ArrayList<>();
  private final CheckedRequirementSet checked = new CheckedRequirementSet();

  /**
   * Create new requirement set
   *
   * @param menu    the menu
   * @param section the section
   */
  public GlobalRequirement(Menu<?> menu, ConfigurationSection section) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section.getValues(false));
    requirements.addAll(RequirementBuilder.getRequirementSet(section, null));
    if (keys.containsKey("fail-command")) {
      commands.addAll(CommandBuilder.getCommands(menu,
          CommonUtils.createStringListFromObject(keys.get("fail-command"), true)));
    }
  }

  /**
   * Check if the player meets all requirements
   *
   * @param player the player
   * @return true if the player does
   */
  public boolean check(Player player) {
    for (RequirementSet requirement : requirements) {
      if (requirement.check(player)) {
        checked.put(player, requirement);
        return true;
      }
    }
    return false;
  }

  /**
   * Get checked requirement set
   *
   * @param player the player
   * @return the checked requirement set
   */
  public Optional<RequirementSet> getCheckedRequirement(Player player) {
    return checked.get(player);
  }

  /**
   * Run the fail commands
   *
   * @param player the player
   */
  public void sendFailCommand(Player player) {
    TaskChain<?> taskChain = BetterGUI.newChain();
    commands.forEach(command -> command.addToTaskChain(player, taskChain));
    taskChain.execute();
  }

  /**
   * Get the list of requirement sets
   *
   * @return the list of requirement sets
   */
  public List<RequirementSet> getRequirements() {
    return requirements;
  }
}
