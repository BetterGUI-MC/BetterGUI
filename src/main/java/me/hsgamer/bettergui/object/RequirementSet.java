package me.hsgamer.bettergui.object;

import co.aikar.taskchain.TaskChain;
import java.util.ArrayList;
import java.util.List;
import me.hsgamer.bettergui.BetterGUI;
import org.bukkit.entity.Player;

public class RequirementSet {

  private final String name;
  private final List<Requirement<?, ?>> requirements;
  private final List<Command> commands = new ArrayList<>();

  public RequirementSet(String name, List<Requirement<?, ?>> requirements) {
    this.name = name;
    this.requirements = requirements;
  }

  public List<Requirement<?, ?>> getRequirements() {
    return requirements;
  }

  public void setCommand(List<Command> commands) {
    this.commands.addAll(commands);
  }

  public boolean check(Player player) {
    for (Requirement<?, ?> requirement : requirements) {
      if (!requirement.check(player)) {
        return false;
      }
    }
    return true;
  }

  public void take(Player player) {
    for (Requirement<?, ?> requirement : requirements) {
      if (requirement.canTake()) {
        requirement.take(player);
      }
    }
  }

  public void sendCommand(Player player) {
    TaskChain<?> taskChain = BetterGUI.newChain();
    commands.forEach(command -> command.addToTaskChain(player, taskChain));
    taskChain.execute();
  }

  public String getName() {
    return name;
  }
}
