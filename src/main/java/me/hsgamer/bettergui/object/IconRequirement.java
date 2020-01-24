package me.hsgamer.bettergui.object;

import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;

public abstract class IconRequirement<T> {

  protected String failMessage;
  protected List<String> values;
  protected Icon icon;
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
    this.values = values;
  }

  public void setValues(String input) {
    List<String> list = Arrays.asList(input.split(";"));
    list.replaceAll(String::trim);
    setValues(list);
  }

  public void canTake(boolean canTake) {
    this.canTake = canTake;
  }

  public boolean canTake() {
    return canTake;
  }
}
