package me.hsgamer.bettergui.object.property.icon.impl;

import co.aikar.taskchain.TaskChain;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.CommandBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.object.CheckedRequirementSet;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconVariable;
import me.hsgamer.bettergui.object.RequirementSet;
import me.hsgamer.bettergui.object.property.IconProperty;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ClickRequirement extends IconProperty<ConfigurationSection> {

  private final Map<ClickType, List<RequirementSet>> requirementsPerClickType = new EnumMap<>(
      ClickType.class);
  private final Map<ClickType, CheckedRequirementSet> checkedMap = new EnumMap<>(ClickType.class);
  private final Map<ClickType, List<Command>> commands = new EnumMap<>(ClickType.class);

  private final List<RequirementSet> defaultRequirements = new ArrayList<>();
  private final List<Command> defaultCommands = new ArrayList<>();
  private final CheckedRequirementSet defaultChecked = new CheckedRequirementSet();

  public ClickRequirement(Icon icon) {
    super(icon);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(getValue().getValues(false));
    // Per Click Type
    for (ClickType clickType : ClickType.values()) {
      String subsection = clickType.name();
      if (keys.containsKey(subsection)) {
        List<RequirementSet> requirements = RequirementBuilder
            .getRequirementSet((ConfigurationSection) keys.get(subsection),
                getIcon());
        requirementsPerClickType.put(clickType, requirements);
        checkedMap.put(clickType, new CheckedRequirementSet());
        registerVariable(clickType.name().toLowerCase(), requirements);

        Map<String, Object> keys1 = new CaseInsensitiveStringMap<>(
            ((ConfigurationSection) keys.get(subsection)).getValues(false));
        if (keys1.containsKey("fail-command")) {
          commands.put(clickType, CommandBuilder.getCommands(getIcon(), CommonUtils
              .createStringListFromObject(keys1.get("fail-command"), true)));
        }
      }
    }
    // Default
    if (keys.containsKey("DEFAULT")) {
      setDefaultRequirements((ConfigurationSection) keys.get("DEFAULT"));
    }
    // Alternative Default
    setDefaultRequirements(getValue());
  }

  private void setDefaultRequirements(ConfigurationSection section) {
    List<RequirementSet> requirements = RequirementBuilder
        .getRequirementSet(section,
            getIcon());
    defaultRequirements.addAll(requirements);
    registerVariable("default", requirements);
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section.getValues(false));
    if (keys.containsKey("fail-command")) {
      defaultCommands.addAll(CommandBuilder.getCommands(getIcon(), CommonUtils
          .createStringListFromObject(keys.get("fail-command"), true)));
    }
  }

  public void sendFailCommand(Player player, ClickType clickType) {
    TaskChain<?> taskChain = BetterGUI.newChain();
    commands.getOrDefault(clickType, defaultCommands)
        .forEach(command -> command.addToTaskChain(player, taskChain));
    taskChain.execute();
  }

  private void registerVariable(String prefix, List<RequirementSet> requirementSets) {
    requirementSets
        .forEach(requirementSet -> requirementSet.getRequirements().forEach(iconRequirement -> {
          if (iconRequirement instanceof IconVariable) {
            getIcon().registerVariable(String.join("_", prefix, requirementSet.getName(),
                ((IconVariable) iconRequirement).getIdentifier()),
                (IconVariable) iconRequirement);
          }
        }));
  }

  public boolean check(Player player, ClickType clickType) {
    List<RequirementSet> requirements = requirementsPerClickType
        .getOrDefault(clickType, defaultRequirements);
    CheckedRequirementSet checkedSet = checkedMap.getOrDefault(clickType, defaultChecked);
    for (RequirementSet requirement : requirements) {
      if (requirement.check(player)) {
        checkedSet.put(player, requirement);
        return true;
      }
    }
    return false;
  }

  public Optional<RequirementSet> getCheckedRequirement(Player player, ClickType clickType) {
    return checkedMap.getOrDefault(clickType, defaultChecked).get(player);
  }
}
