package me.hsgamer.bettergui.requirement;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.process.ProcessApplier;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.task.BatchRunnable;

import java.util.*;

/**
 * The requirement setting used in Menus/Buttons/...
 */
public class RequirementApplier implements ProcessApplier {
  private final List<RequirementSet> requirementSets = new LinkedList<>();
  private final ActionApplier failActionApplier;

  /**
   * Create a new requirement applier
   *
   * @param menu    the menu
   * @param name    the name
   * @param section the section
   */
  public RequirementApplier(Menu menu, String name, Map<String, Object> section) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);
    keys.forEach((key, value) -> {
      if (value instanceof Map) {
        // noinspection unchecked
        requirementSets.add(new RequirementSet(menu, name + "_reqset_" + key, (Map<String, Object>) value));
      }
    });
    this.failActionApplier = new ActionApplier(menu, MapUtil.getIfFoundOrDefault(keys, Collections.emptyList(), "fail-command", "fail-action"));
  }

  @Override
  public void accept(UUID uuid, BatchRunnable.Process process) {
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
    BatchRunnable.TaskPool taskPool = process.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE);
    for (ProcessApplier applier : appliers) {
      taskPool.addLast(process1 -> applier.accept(uuid, process1));
    }
    if (!success) {
      taskPool.addLast(process1 -> failActionApplier.accept(uuid, process1));
      taskPool.addLast(BatchRunnable.Process::complete);
    }
    process.next();
  }
}
