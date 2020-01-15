package me.hsgamer.bettergui.object.property.icon;

import co.aikar.taskchain.TaskChain;
import java.util.ArrayList;
import java.util.List;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.CommandBuilder;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.IconProperty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ClickCommand extends IconProperty<ConfigurationSection> {

  private List<Command> commands = new ArrayList<>();

  public ClickCommand(Icon icon) {
    super(icon);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    if (getValue().isList("")) {
      commands.addAll(CommandBuilder.getCommands(getIcon(), getValue().getStringList("")));
    } else if (getValue().isString("")) {
      commands.addAll(CommandBuilder.getCommands(getIcon(), getValue().getString("")));
    }
  }

  public TaskChain<?> getTaskChain(Player player) {
    TaskChain<?> taskChain = BetterGUI.newChain();
    commands.forEach(command -> command.addToTaskChain(player, taskChain));
    return taskChain;
  }
}
