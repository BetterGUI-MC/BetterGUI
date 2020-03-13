package me.hsgamer.bettergui.object;

import org.bukkit.entity.Player;

/**
 * An abstract class of Requirement Note: You need to set the canTake value when extending this
 * class
 *
 * @param <V> The value type stored from setValue()
 * @param <L> The type of the final value
 */
public abstract class IconRequirement<V, L> {

  protected final Icon icon;
  protected V value;
  private boolean canTake;

  /**
   * The requirement
   *
   * @param icon    the icon involved in
   * @param canTake whether the plugin takes the requirements from the player
   */
  public IconRequirement(Icon icon, boolean canTake) {
    this.icon = icon;
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

  public void canTake(boolean canTake) {
    this.canTake = canTake;
  }

  public boolean canTake() {
    return canTake;
  }
}
