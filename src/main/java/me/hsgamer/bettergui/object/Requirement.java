package me.hsgamer.bettergui.object;

import me.hsgamer.bettergui.manager.VariableManager;
import me.hsgamer.bettergui.object.variable.LocalVariableManager;
import org.bukkit.entity.Player;

/**
 * An abstract class of Requirement Note: You need to set the canTake value when extending this
 * class
 *
 * @param <V> The value type stored from setValue()
 * @param <L> The type of the final value
 */
public abstract class Requirement<V, L> {

  protected V value;
  private LocalVariableManager<?> variableManager;
  private boolean canTake;
  private boolean inverted;

  /**
   * The requirement
   *
   * @param canTake whether the plugin takes the requirements from the player
   */
  public Requirement(boolean canTake) {
    this.canTake = canTake;
  }

  /**
   * Called when getting the final values
   *
   * @param player the player involved in
   * @return the final value
   */
  public abstract L getParsedValue(Player player);

  /**
   * Called when checking if the player meets this requirement
   *
   * @param player the player
   * @return true if the player meets the requirement, otherwise false
   */
  public abstract boolean check(Player player);

  /**
   * Called when taking the requirements from the player
   *
   * @param player the player
   */
  public abstract void take(Player player);

  @SuppressWarnings("unchecked")
  public void setValue(Object value) {
    this.value = (V) value;
  }

  /**
   * Enable/Disable the possibility to take the requirement
   *
   * @param canTake Whether the plugin can take the requirement
   */
  public void canTake(boolean canTake) {
    this.canTake = canTake;
  }

  /**
   * @return Whether the plugin can take the requirement
   */
  public boolean canTake() {
    return canTake;
  }

  /**
   * Get the variable manager from the requirement
   *
   * @return the local variable manager
   */
  protected LocalVariableManager<?> getVariableManager() {
    return variableManager;
  }

  /**
   * Set the variable manager to the requirement
   *
   * @param variableManager the variable manager
   */
  public void setVariableManager(LocalVariableManager<?> variableManager) {
    this.variableManager = variableManager;
  }

  /**
   * Get the parsed string (after replacing the variables)
   *
   * @param input  the string
   * @param player the player involved in
   * @return the parsed string
   */
  protected final String parseFromString(String input, Player player) {
    if (variableManager != null) {
      return variableManager.hasVariables(player, input) ? variableManager
          .setVariables(input, player)
          : input;
    } else {
      return VariableManager.hasVariables(input) ? VariableManager.setVariables(input, player)
          : input;
    }
  }

  /**
   * Whether the requirement is in inverted mode
   *
   * @return true if it is
   */
  public boolean isInverted() {
    return inverted;
  }

  /**
   * Set the inverted mode
   *
   * @param inverted whether it's in inverted mode
   */
  public void setInverted(boolean inverted) {
    this.inverted = inverted;
  }
}
