package me.hsgamer.bettergui.object;

import co.aikar.taskchain.TaskChain;
import java.util.Optional;
import me.hsgamer.bettergui.manager.VariableManager;
import org.bukkit.entity.Player;

public abstract class Command {

  protected final boolean hasVariables;
  private final String command;
  private Icon icon;

  public Command(String command) {
    this.command = command;
    this.hasVariables = VariableManager.hasVariables(command);
  }

  public String getParsedCommand(Player executor) {
    if (icon != null) {
      return hasVariables ? icon.setVariables(command, executor) : command;
    } else {
      return hasVariables ? VariableManager.setVariables(command, executor) : command;
    }
  }

  public abstract void addToTaskChain(Player player, TaskChain<?> taskChain);

  protected Optional<Icon> getIcon() {
    return Optional.ofNullable(icon);
  }

  public void setIcon(Icon icon) {
    this.icon = icon;
  }
}
