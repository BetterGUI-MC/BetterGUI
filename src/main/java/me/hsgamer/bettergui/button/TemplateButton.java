package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.config.TemplateConfig;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.minecraft.gui.button.Button;

import java.util.*;

public class TemplateButton extends BaseWrappedButton<Button> {
  /**
   * Create a new button
   *
   * @param input the input
   */
  public TemplateButton(ButtonBuilder.Input input) {
    super(input);
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
      .ifPresent(map -> map.forEach((k, v) -> {
        String variable = String.valueOf(k);
        String value;
        if (v instanceof List) {
          List<String> list = new ArrayList<>();
          ((List<?>) v).forEach(o -> list.add(String.valueOf(o)));
          value = String.join("\n", list);
        } else {
          value = String.valueOf(v);
        }
        variableMap.put(variable, value);
      }));
    keys.entrySet()
      .stream()
      .filter(entry ->
        !entry.getKey().equalsIgnoreCase("variable")
          && !entry.getKey().equalsIgnoreCase("type")
          && !entry.getKey().equalsIgnoreCase("template")
      )
      .forEach(entry -> finalMap.put(entry.getKey(), entry.getValue()));
    if (!variableMap.isEmpty()) {
      finalMap.replaceAll((s, o) -> TemplateConfig.replaceVariables(o, variableMap));
    }

    return ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(getMenu(), getName(), finalMap)).orElse(null);
  }
}
