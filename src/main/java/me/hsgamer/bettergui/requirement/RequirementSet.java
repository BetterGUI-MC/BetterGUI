package me.hsgamer.bettergui.requirement;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.element.MenuElement;
import me.hsgamer.bettergui.api.process.ProcessApplier;
import me.hsgamer.bettergui.api.replacer.ElementLookupStringReplacer;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.task.element.TaskPool;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The requirement set
 */
public class RequirementSet implements Requirement {
  private final String name;
  private final MenuElement parent;
  private final List<Requirement> requirements;
  private final ActionApplier successActionApplier;
  private final ActionApplier failActionApplier;

  /**
   * Create a new requirement set
   *
   * @param parent  the parent element
   * @param name    the name
   * @param section the section
   */
  public RequirementSet(MenuElement parent, String name, Map<String, Object> section) {
    this.name = name;
    this.parent = parent;
    this.requirements = section.entrySet().stream().flatMap(entry -> {
      String type = entry.getKey();
      Object value = entry.getValue();
      return JavaPlugin.getPlugin(BetterGUI.class).get(RequirementBuilder.class).build(new RequirementBuilder.Input(parent, type, value)).map(Stream::of).orElse(Stream.empty());
    }).collect(Collectors.toList());

    Map<String, Object> keys = MapUtils.createLowercaseStringObjectMap(section);
    this.successActionApplier = Optional.ofNullable(MapUtils.getIfFound(keys, "success-command", "success-action"))
      .map(o -> new ActionApplier(parent, o))
      .orElse(ActionApplier.EMPTY);
    this.failActionApplier = Optional.ofNullable(MapUtils.getIfFound(keys, "fail-command", "fail-action"))
      .map(o -> new ActionApplier(parent, o))
      .orElse(ActionApplier.EMPTY);
  }

  /**
   * Get the success action applier
   *
   * @return the success action applier
   */
  public ActionApplier getSuccessActionApplier() {
    return successActionApplier;
  }

  /**
   * Get the fail action applier
   *
   * @return the fail action applier
   */
  public ActionApplier getFailActionApplier() {
    return failActionApplier;
  }

  @Override
  public Result check(UUID uuid) {
    List<ProcessApplier> processAppliers = new ArrayList<>();
    boolean success = true;
    for (Requirement requirement : requirements) {
      Result result = requirement.check(uuid);
      if (result.isSuccess) {
        processAppliers.add(result.applier);
      } else {
        success = false;
        break;
      }
    }
    if (success) {
      return Result.success((uuid1, process) -> {
        TaskPool taskPool = process.getCurrentTaskPool();
        processAppliers.forEach(processApplier -> taskPool.addLast(subProcess -> processApplier.accept(uuid1, subProcess)));
        successActionApplier.accept(uuid1, process);
      });
    } else {
      return Result.fail(failActionApplier);
    }
  }

  @Override
  public MenuElement getParent() {
    return parent;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public StringReplacer getStringReplacer() {
    return (ElementLookupStringReplacer<Requirement>) () -> requirements;
  }
}
