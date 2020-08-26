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
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.IconProperty;
import me.hsgamer.bettergui.object.requirementset.CheckedRequirementSet;
import me.hsgamer.bettergui.object.requirementset.RequirementSet;
import me.hsgamer.bettergui.object.variable.LocalVariable;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.MenuClickType;
import me.hsgamer.bettergui.util.Validate;
import me.hsgamer.hscore.map.CaseInsensitiveStringMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ClickRequirement extends IconProperty<ConfigurationSection> {

  private static final String FAIL_COMMAND = "fail-command";

  private final Map<MenuClickType, List<RequirementSet>> requirementsPerClickType = new EnumMap<>(
      MenuClickType.class);
  private final Map<MenuClickType, CheckedRequirementSet> checkedSetPerClickType = new EnumMap<>(
      MenuClickType.class);
  private final Map<MenuClickType, List<Command>> failCommandsPerClickType = new EnumMap<>(
      MenuClickType.class);

  public ClickRequirement(Icon icon) {
    super(icon);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(getValue().getValues(false));
    // Per Click Type
    for (MenuClickType clickType : MenuClickType.values()) {
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

  private void setRequirements(MenuClickType clickType, ConfigurationSection section) {
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
    for (MenuClickType clickType : MenuClickType.values()) {
      requirementsPerClickType.putIfAbsent(clickType, requirements);
      checkedSetPerClickType.putIfAbsent(clickType, checkedRequirementSet);
      failCommandsPerClickType.putIfAbsent(clickType, commands);
    }
  }

  public void sendFailCommand(Player player, MenuClickType clickType) {
    TaskChain<?> taskChain = BetterGUI.newChain();
    failCommandsPerClickType.get(clickType)
        .forEach(command -> command.addToTaskChain(player, taskChain));
    taskChain.execute();
  }

  private void registerVariable(String prefix, List<RequirementSet> requirementSets) {
    requirementSets
        .forEach(requirementSet -> requirementSet.getRequirements().forEach(iconRequirement -> {
          if (iconRequirement instanceof LocalVariable) {
            getIcon().registerVariable(String.join("_", prefix, requirementSet.getName(),
                ((LocalVariable) iconRequirement).getIdentifier()),
                (LocalVariable) iconRequirement);
          }
        }));
  }

  public boolean check(Player player, MenuClickType clickType) {
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

  public Optional<RequirementSet> getCheckedRequirement(Player player, MenuClickType clickType) {
    CheckedRequirementSet checkedRequirementSet = checkedSetPerClickType.get(clickType);
    if (checkedRequirementSet == null) {
      return Optional.empty();
    }
    return checkedRequirementSet.get(player);
  }
}
