package me.hsgamer.bettergui.object;

import java.util.Optional;
import me.hsgamer.bettergui.manager.VariableManager;
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
  private Icon icon;
  private boolean canTake;

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
   * Get the icon involved in this command
   *
   * @return the icon
   */
  protected Optional<Icon> getIcon() {
    return Optional.ofNullable(icon);
  }

  /**
   * Set the icon to this command
   *
   * @param icon the icon
   */
  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  /**
   * Get the parsed string (after replacing the variables)
   *
   * @param input  the string
   * @param player the player involved in
   * @return the parsed string
   */
  protected final String parseFromString(String input, Player player) {
    if (icon != null) {
      return icon.hasVariables(input) ? icon.setVariables(input, player) : input;
    } else {
      return VariableManager.hasVariables(input) ? VariableManager.setVariables(input, player)
          : input;
    }
  }
}
