package me.hsgamer.bettergui.object.property.icon.impl;

import co.aikar.taskchain.TaskChain;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.CommandBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.IconProperty;
import me.hsgamer.bettergui.object.requirementset.CheckedRequirementSet;
import me.hsgamer.bettergui.object.requirementset.RequirementSet;
import me.hsgamer.hscore.common.CommonUtils;
import me.hsgamer.hscore.map.CaseInsensitiveStringMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ViewRequirement extends IconProperty<ConfigurationSection> {

  private final List<RequirementSet> requirements = new ArrayList<>();
  private final List<Command> commands = new ArrayList<>();
  private final CheckedRequirementSet checked = new CheckedRequirementSet();

  public ViewRequirement(Icon icon) {
    super(icon);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(getValue().getValues(false));
    requirements.addAll(RequirementBuilder.getRequirementSet(getValue(), getIcon()));
    if (keys.containsKey("fail-command")) {
      commands.addAll(CommandBuilder.getCommands(getIcon(),
          CommonUtils.createStringListFromObject(keys.get("fail-command"), true)));
    }
  }

  public boolean check(Player player) {
    for (RequirementSet requirement : requirements) {
      if (requirement.check(player)) {
        checked.put(player, requirement);
        return true;
      }
    }
    return false;
  }

  public Optional<RequirementSet> getCheckedRequirement(Player player) {
    return checked.get(player);
  }

  public void sendFailCommand(Player player) {
    TaskChain<?> taskChain = BetterGUI.newChain();
    commands.forEach(command -> command.addToTaskChain(player, taskChain));
    taskChain.execute();
  }
}
