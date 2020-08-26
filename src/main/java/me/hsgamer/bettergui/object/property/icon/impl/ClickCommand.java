package me.hsgamer.bettergui.object.property.icon.impl;

import co.aikar.taskchain.TaskChain;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.CommandBuilder;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.IconProperty;
import me.hsgamer.bettergui.util.MenuClickType;
import me.hsgamer.hscore.common.CommonUtils;
import me.hsgamer.hscore.map.CaseInsensitiveStringMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ClickCommand extends IconProperty<Object> {

  private final List<Command> defaultCommands = new ArrayList<>();
  private final Map<MenuClickType, List<Command>> commandsPerClickType = new EnumMap<>(
      MenuClickType.class);

  public ClickCommand(Icon icon) {
    super(icon);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    if (getValue() instanceof ConfigurationSection) {
      Map<String, Object> keys = new CaseInsensitiveStringMap<>(
          ((ConfigurationSection) getValue()).getValues(false));
      for (MenuClickType clickType : MenuClickType.values()) {
        String subsection = clickType.name();
        if (keys.containsKey(subsection)) {
          List<Command> commands = new ArrayList<>(
              CommandBuilder.getCommands(getIcon(),
                  CommonUtils.createStringListFromObject(keys.get(subsection), true)));
          commandsPerClickType.put(clickType, commands);
        }
      }
      if (keys.containsKey("DEFAULT")) {
        List<Command> commands = new ArrayList<>(
            CommandBuilder.getCommands(getIcon(),
                CommonUtils.createStringListFromObject(keys.get("DEFAULT"), true)));
        defaultCommands.addAll(commands);
      }
    } else {
      defaultCommands.addAll(CommandBuilder
          .getCommands(getIcon(), CommonUtils.createStringListFromObject(getValue(), true)));
    }
  }

  public <T> TaskChain<T> getTaskChain(Player player, MenuClickType clickType) {
    TaskChain<T> taskChain = BetterGUI.newChain();
    commandsPerClickType.getOrDefault(clickType, defaultCommands)
        .forEach(command -> command.addToTaskChain(player, taskChain));
    return taskChain;
  }
}
