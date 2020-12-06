package me.hsgamer.bettergui.requirementset;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.bettergui.api.requirement.Requirement;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * The requirement set
 */
public class RequirementSet implements MenuElement {
  private final String name;
  private final List<Requirement> requirements;
  private final List<Action> successActions = new LinkedList<>();
  private final List<Action> failActions = new LinkedList<>();
  private final Menu menu;

  public RequirementSet(String name, Menu menu, List<Requirement> requirements) {
    this.name = name;
    this.menu = menu;
    this.requirements = requirements;
  }

  /**
   * Get the requirements
   *
   * @return the requirements
   */
  public List<Requirement> getRequirements() {
    return requirements;
  }

  /**
   * Set the success actions
   *
   * @param actions the actions
   */
  public void setSuccessActions(List<Action> actions) {
    this.successActions.addAll(actions);
  }

  /**
   * Set the fail commands
   *
   * @param actions the commands
   */
  public void setFailActions(List<Action> actions) {
    this.failActions.addAll(actions);
  }

  /**
   * Check if the unique id meets all requirements
   *
   * @param uuid the unique id
   *
   * @return true if it does
   */
  public boolean check(UUID uuid) {
    for (Requirement requirement : requirements) {
      if (!requirement.check(uuid)) {
        TaskChain<?> taskChain = BetterGUI.newChain();
        failActions.forEach(command -> command.addToTaskChain(uuid, taskChain));
        taskChain.execute();
        return false;
      }
    }
    return true;
  }

  /**
   * Run the "take" action of the requirement
   *
   * @param uuid the id to "take"
   */
  public void take(UUID uuid) {
    for (Requirement requirement : requirements) {
      requirement.take(uuid);
    }
  }

  /**
   * Run success actions
   *
   * @param uuid the player
   */
  public void sendSuccessActions(UUID uuid) {
    TaskChain<?> taskChain = BetterGUI.newChain();
    successActions.forEach(command -> command.addToTaskChain(uuid, taskChain));
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

  @Override
  public Menu getMenu() {
    return menu;
  }
}
