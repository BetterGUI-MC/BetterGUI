package me.hsgamer.bettergui.requirement;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.process.ProcessApplier;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.hscore.bukkit.clicktype.BukkitClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.task.element.TaskPool;
import me.hsgamer.hscore.task.element.TaskProcess;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The requirement setting used in Menus/Buttons/...
 */
public class RequirementApplier implements ProcessApplier {
  /**
   * The empty requirement applier
   */
  public static final RequirementApplier EMPTY = new RequirementApplier(Collections.emptyList(), ActionApplier.EMPTY);

  private final List<RequirementSet> requirementSets;
  private final ActionApplier failActionApplier;

  /**
   * Create a new requirement applier
   *
   * @param requirementSets   the requirement sets
   * @param failActionApplier the fail action applier
   */
  public RequirementApplier(List<RequirementSet> requirementSets, ActionApplier failActionApplier) {
    this.requirementSets = requirementSets;
    this.failActionApplier = failActionApplier;
  }

  /**
   * Create a new requirement applier
   *
   * @param menu    the menu
   * @param name    the name
   * @param section the section
   */
  public RequirementApplier(Menu menu, String name, Map<String, Object> section) {
    this.requirementSets = new ArrayList<>();
    Map<String, Object> keys = MapUtils.createLowercaseStringObjectMap(section);
    keys.forEach((key, value) -> {
      if (value instanceof Map) {
        // noinspection unchecked
        requirementSets.add(new RequirementSet(menu, name + "_reqset_" + key, (Map<String, Object>) value));
      }
    });
    this.failActionApplier = Optional.ofNullable(MapUtils.getIfFound(keys, "fail-command", "fail-action"))
      .map(o -> new ActionApplier(menu, o))
      .orElse(ActionApplier.EMPTY);
  }

  /**
   * Convert the section to a map of click requirement appliers
   *
   * @param section the section
   * @param button  the button
   *
   * @return the map
   */
  public static Map<BukkitClickType, RequirementApplier> convertClickRequirementAppliers(Map<String, Object> section, WrappedButton button) {
    Map<BukkitClickType, RequirementApplier> clickRequirements = new ConcurrentHashMap<>();

    Map<String, BukkitClickType> clickTypeMap = ClickTypeUtils.getClickTypeMap();
    Map<String, Object> keys = MapUtils.createLowercaseStringObjectMap(section);

    boolean simpleInput = true;
    List<BukkitClickType> remainingClickTypes = new ArrayList<>();

    for (Map.Entry<String, BukkitClickType> entry : clickTypeMap.entrySet()) {
      String clickTypeName = entry.getKey().toLowerCase(Locale.ROOT);
      BukkitClickType clickType = entry.getValue();
      Optional<Map<String, Object>> optionalSubSection = Optional.ofNullable(keys.get(clickTypeName)).flatMap(MapUtils::castOptionalStringObjectMap);
      if (!optionalSubSection.isPresent()) {
        remainingClickTypes.add(clickType);
        continue;
      }
      simpleInput = false;
      clickRequirements.put(clickType, new RequirementApplier(
        button.getMenu(),
        button.getName() + "_click_" + clickTypeName,
        optionalSubSection.get()
      ));
    }

    RequirementApplier defaultSetting = new RequirementApplier(
      button.getMenu(),
      button.getName() + "_click_default",
      Optional.ofNullable(keys.get("default"))
        .flatMap(MapUtils::castOptionalStringObjectMap)
        .orElse(simpleInput ? section : Collections.emptyMap())
    );

    for (BukkitClickType clickType : remainingClickTypes) {
      clickRequirements.put(clickType, defaultSetting);
    }

    return clickRequirements;
  }

  /**
   * Check if the applier is empty
   *
   * @return true if it is empty
   */
  public boolean isEmpty() {
    return requirementSets.isEmpty() && failActionApplier.isEmpty();
  }

  /**
   * Get the result of the requirement
   *
   * @param uuid the unique id
   *
   * @return the result
   */
  public Requirement.Result getResult(UUID uuid) {
    List<ProcessApplier> appliers;
    boolean success;
    if (requirementSets.isEmpty()) {
      appliers = Collections.emptyList();
      success = true;
    } else {
      appliers = new ArrayList<>();
      success = false;
      for (RequirementSet requirementSet : requirementSets) {
        Requirement.Result result = requirementSet.check(uuid);
        appliers.add(result.applier);
        if (result.isSuccess) {
          success = true;
          break;
        }
      }
    }
    if (!success) {
      appliers.add(failActionApplier);
    }
    return new Requirement.Result(success, (uuid1, process) -> {
      for (ProcessApplier applier : appliers) {
        process.getCurrentTaskPool().addLast(process1 -> applier.accept(uuid1, process1));
      }
    });
  }

  @Override
  public void accept(UUID uuid, TaskProcess process) {
    Requirement.Result result = getResult(uuid);
    TaskPool taskPool = process.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE);
    taskPool.addLast(process1 -> result.applier.accept(uuid, process1));
    process.next();
  }
}
