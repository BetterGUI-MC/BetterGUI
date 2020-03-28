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
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ClickRequirement extends IconProperty<ConfigurationSection> {

  private static final String FAIL_COMMAND = "fail-command";

  private final Map<ClickType, List<RequirementSet>> requirementsPerClickType = new EnumMap<>(
      ClickType.class);
  private final Map<ClickType, CheckedRequirementSet> checkedSetPerClickType = new EnumMap<>(
      ClickType.class);
  private final Map<ClickType, List<Command>> failCommandsPerClickType = new EnumMap<>(
      ClickType.class);

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
        setRequirements(clickType, (ConfigurationSection) keys.get(subsection));
      }
    }
    // Default
    if (keys.containsKey("DEFAULT")) {
      setDefaultRequirements((ConfigurationSection) keys.get("DEFAULT"));
    }
  }

  private void setRequirements(ClickType clickType, ConfigurationSection section) {
    List<RequirementSet> requirements = RequirementBuilder
        .getRequirementSet(section,
            getIcon());
    requirementsPerClickType.put(clickType, requirements);
    checkedSetPerClickType.put(clickType, new CheckedRequirementSet());
    registerVariable(clickType.name().toLowerCase(), requirements);

    Map<String, Object> keys1 = new CaseInsensitiveStringMap<>((section).getValues(false));
    List<Command> commands = new ArrayList<>();
    if (keys1.containsKey(FAIL_COMMAND)) {
      commands.addAll(CommandBuilder.getCommands(getIcon(),
          CommonUtils.createStringListFromObject(keys1.get(FAIL_COMMAND), true)));
    }
    failCommandsPerClickType.put(clickType, commands);
  }

  private void setDefaultRequirements(ConfigurationSection section) {
    List<RequirementSet> requirements = RequirementBuilder
        .getRequirementSet(section,
            getIcon());
    List<Command> commands = new ArrayList<>();
    CheckedRequirementSet checkedRequirementSet = new CheckedRequirementSet();
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section.getValues(false));
    if (keys.containsKey(FAIL_COMMAND)) {
      commands.addAll(CommandBuilder.getCommands(getIcon(), CommonUtils
          .createStringListFromObject(keys.get(FAIL_COMMAND), true)));
    }
    registerVariable("default", requirements);
    for (ClickType clickType : ClickType.values()) {
      requirementsPerClickType.putIfAbsent(clickType, requirements);
      checkedSetPerClickType.putIfAbsent(clickType, checkedRequirementSet);
      failCommandsPerClickType.putIfAbsent(clickType, commands);
    }
  }

  public void sendFailCommand(Player player, ClickType clickType) {
    TaskChain<?> taskChain = BetterGUI.newChain();
    failCommandsPerClickType.get(clickType)
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
        .get(clickType);
    if (Validate.isNullOrEmpty(requirements)) {
      return true;
    }
    CheckedRequirementSet checkedSet = checkedSetPerClickType.get(clickType);
    for (RequirementSet requirement : requirements) {
      if (requirement.check(player)) {
        checkedSet.put(player, requirement);
        return true;
      }
    }
    return false;
  }

  public Optional<RequirementSet> getCheckedRequirement(Player player, ClickType clickType) {
    CheckedRequirementSet checkedRequirementSet = checkedSetPerClickType.get(clickType);
    if (checkedRequirementSet == null) {
      return Optional.empty();
    }
    return checkedRequirementSet.get(player);
  }
}
