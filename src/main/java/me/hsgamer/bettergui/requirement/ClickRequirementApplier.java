package me.hsgamer.bettergui.requirement;

import me.hsgamer.bettergui.api.element.MenuElement;
import me.hsgamer.bettergui.api.replacer.LookupStringReplacer;
import me.hsgamer.hscore.bukkit.clicktype.BukkitClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.common.StringReplacer;

import java.util.*;

public class ClickRequirementApplier implements MenuElement {
  private final MenuElement parent;
  private final Map<BukkitClickType, RequirementApplier> requirementAppliers;
  private final RequirementApplier defaultApplier;

  public ClickRequirementApplier(MenuElement parent, Map<String, Object> section) {
    this.parent = parent;
    Map<BukkitClickType, RequirementApplier> requirementAppliers = new HashMap<>();

    Map<String, BukkitClickType> clickTypeMap = ClickTypeUtils.getClickTypeMap();
    Map<String, Object> keys = MapUtils.createLowercaseStringObjectMap(section);

    boolean simpleInput = true;

    for (Map.Entry<String, BukkitClickType> entry : clickTypeMap.entrySet()) {
      String clickTypeName = entry.getKey().toLowerCase(Locale.ROOT);
      BukkitClickType clickType = entry.getValue();
      Optional<Map<String, Object>> optionalSubSection = Optional.ofNullable(keys.get(clickTypeName)).flatMap(MapUtils::castOptionalStringObjectMap);
      if (!optionalSubSection.isPresent()) {
        continue;
      }
      simpleInput = false;
      requirementAppliers.put(clickType, new RequirementApplier(
        parent,
        optionalSubSection.get()
      ));
    }

    this.defaultApplier = new RequirementApplier(
      parent,
      Optional.ofNullable(keys.get("default"))
        .flatMap(MapUtils::castOptionalStringObjectMap)
        .orElse(simpleInput ? section : Collections.emptyMap())
    );

    this.requirementAppliers = requirementAppliers;
  }

  public RequirementApplier getRequirementApplier(BukkitClickType clickType) {
    return requirementAppliers.getOrDefault(clickType, defaultApplier);
  }

  public boolean exists() {
    return !requirementAppliers.isEmpty() || !defaultApplier.isEmpty();
  }

  @Override
  public StringReplacer getStringReplacer() {
    return (LookupStringReplacer) original -> {
      if (original.startsWith("default")) {
        return Pair.of(defaultApplier.getStringReplacer(), original.substring("default".length()));
      }
      String lowerCaseOriginal = original.toLowerCase(Locale.ROOT);
      for (Map.Entry<BukkitClickType, RequirementApplier> entry : requirementAppliers.entrySet()) {
        String name = entry.getKey().getName().toLowerCase(Locale.ROOT);
        if (lowerCaseOriginal.startsWith(name)) {
          return Pair.of(entry.getValue().getStringReplacer(), name);
        }
      }
      return null;
    };
  }

  @Override
  public MenuElement getParent() {
    return parent;
  }

  @Override
  public String getName() {
    return "click_requirement";
  }
}
