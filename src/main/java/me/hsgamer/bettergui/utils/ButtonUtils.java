package me.hsgamer.bettergui.utils;

import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.requirement.RequirementSetting;
import me.hsgamer.hscore.bukkit.clicktype.AdvancedClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ButtonUtils {
  private ButtonUtils() {
    // EMPTY
  }

  public static Map<AdvancedClickType, RequirementSetting> convertClickRequirements(Map<String, Object> section, WrappedButton button) {
    Map<AdvancedClickType, RequirementSetting> clickRequirements = new ConcurrentHashMap<>();

    Map<String, AdvancedClickType> clickTypeMap = ClickTypeUtils.getClickTypeMap();
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);

    RequirementSetting defaultSetting = new RequirementSetting(button.getMenu(), button.getName() + "_click_default");
    Optional.ofNullable(keys.get("default"))
      .filter(Map.class::isInstance)
      .<Map<String, Object>>map(Map.class::cast)
      .ifPresent(defaultSetting::loadFromSection);

    clickTypeMap.forEach((clickTypeName, clickType) ->
      clickRequirements.put(clickType, Optional.ofNullable(keys.get(clickTypeName))
        .filter(Map.class::isInstance)
        .<Map<String, Object>>map(Map.class::cast)
        .map(subsection -> {
          RequirementSetting setting = new RequirementSetting(button.getMenu(), button.getName() + "_click_" + clickTypeName.toLowerCase(Locale.ROOT));
          setting.loadFromSection(subsection);
          return setting;
        }).orElse(defaultSetting))
    );

    return clickRequirements;
  }

  public static Map<AdvancedClickType, List<Action>> convertActions(Object o, WrappedButton button) {
    Map<AdvancedClickType, List<Action>> actionMap = new HashMap<>();
    Map<String, AdvancedClickType> clickTypeMap = ClickTypeUtils.getClickTypeMap();
    if (o instanceof Map) {
      // noinspection unchecked
      Map<String, Object> keys = new CaseInsensitiveStringMap<>((Map<String, Object>) o);
      List<Action> defaultActions = Optional.ofNullable(keys.get("default")).map(value -> ActionBuilder.INSTANCE.getActions(button.getMenu(), value)).orElse(Collections.emptyList());
      clickTypeMap.forEach((clickTypeName, clickType) -> {
        if (keys.containsKey(clickTypeName)) {
          actionMap.put(clickType, ActionBuilder.INSTANCE.getActions(button.getMenu(), keys.get(clickTypeName)));
        } else {
          actionMap.put(clickType, defaultActions);
        }
      });
    } else {
      clickTypeMap.values().forEach(advancedClickType -> actionMap.put(advancedClickType, ActionBuilder.INSTANCE.getActions(button.getMenu(), o)));
    }
    return actionMap;
  }
}
