package me.hsgamer.bettergui.object;

import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;

public abstract class IconRequirement<T> {

  protected final Icon icon;
  protected String failMessage;
  protected List<String> values;
  private boolean canTake;

  public IconRequirement(Icon icon, boolean canTake) {
    this.icon = icon;
    this.canTake = canTake;
  }

  public abstract List<T> getParsedValue(Player player);

  public abstract boolean check(Player player);

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
