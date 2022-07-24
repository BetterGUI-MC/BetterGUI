package me.hsgamer.bettergui.requirement;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.process.ProcessApplier;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The requirement set
 */
public class RequirementSet implements Requirement {
  private final String name;
  private final Menu menu;
  private final List<Requirement> requirements;
  private final ActionApplier successActionApplier;
  private final ActionApplier failActionApplier;

  /**
   * Create a new requirement set
   *
   * @param menu    the menu
   * @param name    the name
   * @param section the section
   */
  public RequirementSet(Menu menu, String name, Map<String, Object> section) {
    this.name = name;
    this.menu = menu;
    this.requirements = section.entrySet().stream().flatMap(entry -> {
      String type = entry.getKey();
      Object value = entry.getValue();
      return RequirementBuilder.INSTANCE.build(new RequirementBuilder.Input(menu, type, name + "_" + type, value)).map(Stream::of).orElse(Stream.empty());
    }).collect(Collectors.toList());

    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);
    this.successActionApplier = new ActionApplier(menu, MapUtil.getIfFoundOrDefault(keys, Collections.emptyList(), "success-command", "success-action"));
    this.failActionApplier = new ActionApplier(menu, MapUtil.getIfFoundOrDefault(keys, Collections.emptyList(), "fail-command", "fail-action"));
  }

  /**
   * Get the requirements
   *
   * @return the requirements
   */
  public List<Requirement> getRequirements() {
    return requirements;
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
        processAppliers.forEach(processApplier -> processApplier.accept(uuid1, process));
        successActionApplier.accept(uuid1, process);
      });
    } else {
      return Result.fail(failActionApplier);
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Menu getMenu() {
    return menu;
  }
}
