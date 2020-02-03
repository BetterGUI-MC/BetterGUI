package me.hsgamer.bettergui.object;

import co.aikar.taskchain.TaskChain;
import java.util.Optional;
import me.hsgamer.bettergui.manager.VariableManager;
import org.bukkit.entity.Player;

public abstract class Command {

  protected final boolean hasVariables;
  private final String string;
  private Icon icon;

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
  public String getParsedCommand(Player executor) {
    if (icon != null) {
      return hasVariables ? icon.setVariables(string, executor) : string;
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
}
