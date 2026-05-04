package me.hsgamer.bettergui.requirement;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.element.MenuElement;
import me.hsgamer.bettergui.api.element.WithElementLookupStringReplacer;
import me.hsgamer.bettergui.api.process.ProcessApplier;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.task.element.TaskPool;
import me.hsgamer.hscore.task.element.TaskProcess;

import java.util.*;

/**
 * The requirement setting used in Menus/Buttons/...
 */
public class RequirementApplier implements ProcessApplier, MenuElement, WithElementLookupStringReplacer<RequirementSet> {
  private final MenuElement parent;
  private final List<RequirementSet> requirementSets;
  private final ActionApplier failActionApplier;

  /**
   * Create a new requirement applier
   *
   * @param parent            the parent element
   * @param requirementSets   the requirement sets
   * @param failActionApplier the fail action applier
   */
  public RequirementApplier(MenuElement parent, List<RequirementSet> requirementSets, ActionApplier failActionApplier) {
    this.parent = parent;
    this.requirementSets = requirementSets;
    this.failActionApplier = failActionApplier;
  }

  /**
   * Create a new requirement applier
   *
   * @param parent  the parent element
   * @param section the section
   */
  public RequirementApplier(MenuElement parent, Map<String, Object> section) {
    this.parent = parent;
    this.requirementSets = new ArrayList<>();
    Map<String, Object> keys = MapUtils.createLowercaseStringObjectMap(section);
    keys.forEach((key, value) -> {
      if (value instanceof Map) {
        // noinspection unchecked
        requirementSets.add(new RequirementSet(parent, key, (Map<String, Object>) value));
      }
    });
    this.failActionApplier = Optional.ofNullable(MapUtils.getIfFound(keys, "fail-command", "fail-action"))
      .map(o -> new ActionApplier(parent, o))
      .orElse(ActionApplier.EMPTY);
  }

  /**
   * Create an empty requirement applier
   *
   * @param parent the parent element
   *
   * @return the requirement applier
   */
  public static RequirementApplier empty(MenuElement parent) {
    return new RequirementApplier(parent, Collections.emptyList(), ActionApplier.EMPTY);
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

  @Override
  public List<RequirementSet> getElements() {
    return requirementSets;
  }

  @Override
  public String getPrefix() {
    return "reqset_";
  }

  @Override
  public MenuElement getParent() {
    return parent;
  }

  @Override
  public String getName() {
    return "requirement_applier";
  }
}
