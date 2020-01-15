package me.hsgamer.bettergui.object;

import co.aikar.taskchain.TaskChain;
import org.bukkit.entity.Player;

public abstract class Command {

  protected final boolean hasVariables;
  private final String command;
  private final Icon icon;

  public Command(Icon icon, String command) {
    this.command = command;
    this.icon = icon;
    this.hasVariables = true;
  }

  public String getParsedCommand(Player executor) {
    return icon.hasVariables(command) ? icon.setVariables(command, executor) : command;
  }

  public abstract void addToTaskChain(Player player, TaskChain<?> taskChain);

}
