package me.hsgamer.bettergui.object;

import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;

/**
 * An abstract class of Requirement
 * Note: You need to set the canTake value when extending this class
 *
 * @param <T> The type of the final value
 */
public abstract class IconRequirement<T> {

  protected final Icon icon;
  protected String failMessage;
  protected List<String> values;
  private boolean canTake;

  /**
   * The requirement
   *
   * @param icon the icon involved in
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
   * @return a list of final values
   */
  public abstract List<T> getParsedValue(Player player);

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

  public void setFailMessage(String message) {
    this.failMessage = message;
  }

  public void setValues(List<String> values) {
    values.replaceAll(String::trim);
    this.values = values;
  }

  public void setValues(String input) {
    setValues(Arrays.asList(input.split(";")));
  }

  public void canTake(boolean canTake) {
    this.canTake = canTake;
  }

  public boolean canTake() {
    return canTake;
  }
}
