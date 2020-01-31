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

  public String getParsedCommand(Player executor) {
    if (icon != null) {
      return hasVariables ? icon.setVariables(string, executor) : string;
    } else {
      return hasVariables ? VariableManager.setVariables(string, executor) : string;
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
