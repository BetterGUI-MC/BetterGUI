package me.hsgamer.bettergui.object.requirementset;

import co.aikar.taskchain.TaskChain;
import java.util.ArrayList;
import java.util.List;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Requirement;
import org.bukkit.entity.Player;

/**
 * The Requirement Set
 */
public class RequirementSet {

  private final String name;
  private final List<Requirement<?, ?>> requirements;
  private final List<Command> successCommands = new ArrayList<>();
  private final List<Command> failCommands = new ArrayList<>();

  public RequirementSet(String name, List<Requirement<?, ?>> requirements) {
    this.name = name;
    this.requirements = requirements;
  }

  /**
   * Get the requirements
   *
   * @return the requirements
   */
  public List<Requirement<?, ?>> getRequirements() {
    return requirements;
  }

  /**
   * Set the success commands
   *
   * @param commands the commands
   */
  public void setSuccessCommands(List<Command> commands) {
    this.successCommands.addAll(commands);
  }

  /**
   * Set the fail commands
   *
   * @param commands the commands
   */
  public void setFailCommands(List<Command> commands) {
    this.failCommands.addAll(commands);
  }

  /**
   * Check if the player meets all requirements
   *
   * @param player the player
   * @return true if the player does
   */
  public boolean check(Player player) {
    for (Requirement<?, ?> requirement : requirements) {
      if (!requirement.check(player)) {
        TaskChain<?> taskChain = BetterGUI.newChain();
        failCommands.forEach(command -> command.addToTaskChain(player, taskChain));
        taskChain.execute();
        return false;
      }
    }
    return true;
  }

  /**
   * Run the "take" action of the requirement
   *
   * @param player the player to "take"
   */
  public void take(Player player) {
    for (Requirement<?, ?> requirement : requirements) {
      if (requirement.canTake()) {
        requirement.take(player);
      }
    }
  }

  /**
   * Run success commands
   *
   * @param player the player
   */
  public void sendSuccessCommands(Player player) {
    TaskChain<?> taskChain = BetterGUI.newChain();
    successCommands.forEach(command -> command.addToTaskChain(player, taskChain));
    taskChain.execute();
  }

  /**
   * Get the name of the set
   *
   * @return the name
   */
  public String getName() {
    return name;
  }
}
