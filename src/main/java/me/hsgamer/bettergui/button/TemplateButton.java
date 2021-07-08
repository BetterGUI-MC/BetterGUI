package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.*;

public class TemplateButton extends BaseWrappedButton {
  /**
   * Create a new button
   *
   * @param menu the menu
   */
  public TemplateButton(Menu menu) {
    super(menu);
  }

  @Override
  protected Button createButton(Map<String, Object> section) {
    Map<String, Object> finalMap = new LinkedHashMap<>();

    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);
    Optional.ofNullable(keys.get("template"))
      .map(String::valueOf)
      .flatMap(s -> BetterGUI.getInstance().getTemplateButtonConfig().get(s))
      .ifPresent(finalMap::putAll);
    Map<String, String> variableMap = new HashMap<>();
    // noinspection unchecked
    Optional.ofNullable(keys.get("variable"))
      .filter(Map.class::isInstance)
      .map(Map.class::cast)
      .ifPresent(map -> map.forEach((k, v) -> variableMap.put(String.valueOf(k), String.valueOf(v))));
    keys.entrySet()
      .stream()
      .filter(entry ->
        !entry.getKey().equalsIgnoreCase("variable")
          && !entry.getKey().equalsIgnoreCase("type")
          && !entry.getKey().equalsIgnoreCase("template")
      )
      .forEach(entry -> finalMap.put(entry.getKey(), entry.getValue()));
    if (!variableMap.isEmpty()) {
      finalMap.replaceAll((s, o) -> replaceVariables(o, variableMap));
    }

    return ButtonBuilder.INSTANCE.getButton(getMenu(), getName(), finalMap);
  }

  private Object replaceVariables(Object obj, Map<String, String> variableMap) {
    if (obj instanceof String) {
      String string = (String) obj;
      for (Map.Entry<String, String> entry : variableMap.entrySet()) {
        string = string.replace("{" + entry.getKey() + "}", entry.getValue());
      }
      return string;
    } else if (obj instanceof Collection) {
      List<Object> replaceList = new ArrayList<>();
      // noinspection unchecked
      ((Collection) obj).forEach(o -> replaceList.add(replaceVariables(o, variableMap)));
      return replaceList;
    } else if (obj instanceof Map) {
      // noinspection unchecked
      ((Map) obj).replaceAll((k, v) -> replaceVariables(v, variableMap));
    }
    return obj;
  }
}
