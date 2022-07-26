package me.hsgamer.bettergui.requirement;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.process.ProcessApplier;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.hscore.bukkit.clicktype.AdvancedClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.task.BatchRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

  /**
   * Convert the section to a map of click requirement appliers
   *
   * @param section the section
   * @param button  the button
   *
   * @return the map
   */
  public static Map<AdvancedClickType, RequirementApplier> convertClickRequirementAppliers(Map<String, Object> section, WrappedButton button) {
    Map<AdvancedClickType, RequirementApplier> clickRequirements = new ConcurrentHashMap<>();

    Map<String, AdvancedClickType> clickTypeMap = ClickTypeUtils.getClickTypeMap();
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);

    RequirementApplier defaultSetting = new RequirementApplier(
      button.getMenu(),
      button.getName() + "_click_default",
      Optional.ofNullable(keys.get("default")).filter(Map.class::isInstance).<Map<String, Object>>map(Map.class::cast).orElse(Collections.emptyMap())
    );

    clickTypeMap.forEach((clickTypeName, clickType) ->
      clickRequirements.put(clickType, Optional.ofNullable(keys.get(clickTypeName))
        .filter(Map.class::isInstance)
        .<Map<String, Object>>map(Map.class::cast)
        .map(subsection -> new RequirementApplier(button.getMenu(), button.getName() + "_click_" + clickTypeName.toLowerCase(Locale.ROOT), subsection))
        .orElse(defaultSetting))
    );

    return clickRequirements;
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
    return new Requirement.Result(success, (uuid1, process) -> {
      for (ProcessApplier applier : appliers) {
        process.getCurrentTaskPool().addLast(process1 -> applier.accept(uuid1, process1));
      }
    });
  }

  @Override
  public void accept(UUID uuid, BatchRunnable.Process process) {
    Requirement.Result result = getResult(uuid);
    BatchRunnable.TaskPool taskPool = process.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE);
    taskPool.addLast(process1 -> {
      result.applier.accept(uuid, process1);
      if (!result.isSuccess) {
        failActionApplier.accept(uuid, process1);
        process1.getCurrentTaskPool().addLast(BatchRunnable.Process::complete);
      }
    });
    process.next();
  }
}
