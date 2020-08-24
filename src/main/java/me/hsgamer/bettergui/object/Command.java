package me.hsgamer.bettergui.object;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.manager.VariableManager;
import me.hsgamer.bettergui.object.variable.LocalVariableManager;
import org.bukkit.entity.Player;

public abstract class Command {

  protected final boolean hasVariables;
  private final String string;
  private LocalVariableManager<?> variableManager;

  public Command(String string) {
    this.string = string;
    this.hasVariables = VariableManager.hasVariables(string);
  }

  /**
   * Get the parsed command (after replacing the variables)
   *
   * @param executor the player involved in
   * @return the parsed command
   */
  protected String getParsedCommand(Player executor) {
    if (variableManager != null) {
      return variableManager.hasVariables(executor, string) ? variableManager
          .setVariables(string, executor) : string;
    } else {
      return hasVariables ? VariableManager.setVariables(string, executor) : string;
    }
  }

  /**
   * Add the executable code to taskChain
   *
   * @param player    the player involved in
   * @param taskChain the TaskChain that needs adding
   */
  public abstract void addToTaskChain(Player player, TaskChain<?> taskChain);

  /**
   * Get the variable manager from the command
   *
   * @return the local variable manager
   */
  protected LocalVariableManager<?> getVariableManager() {
    return variableManager;
  }

  /**
   * Set the variable manager to the command
   *
   * @param variableManager the icon
   */
  public void setVariableManager(LocalVariableManager<?> variableManager) {
    this.variableManager = variableManager;
  }
}
