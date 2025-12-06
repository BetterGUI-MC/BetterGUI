package me.hsgamer.bettergui.argument;

import me.hsgamer.bettergui.api.argument.ArgumentProcessor;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ArgumentProcessorBuilder;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.common.StringReplacer;

import java.util.*;

/**
 * The handler for arguments
 */
public class ArgumentHandler implements ArgumentProcessor {
  private final Menu menu;
  private final Map<String, ArgumentProcessor> processorMap = new LinkedHashMap<>();

  /**
   * Create a new handler
   *
   * @param menu the menu
   */
  public ArgumentHandler(Menu menu, Map<String, Object> section) {
    this.menu = menu;
    Map<String, Object> keys = MapUtils.createLowercaseStringObjectMap(section);
    keys.forEach((key, value) -> {
      if (value instanceof Map) {
        //noinspection unchecked
        Map<String, Object> map = (Map<String, Object>) value;
        ArgumentProcessorBuilder.INSTANCE
          .build(new ArgumentProcessorBuilder.Input(menu, key, map))
          .ifPresent(processor -> processorMap.put(key, processor));
      }
    });
    menu.getVariableManager().register("arg_", StringReplacer.of(this::getValue));
  }

  @Override
  public Optional<String[]> process(UUID uuid, String[] args) {
    for (ArgumentProcessor processor : processorMap.values()) {
      Optional<String[]> optional = processor.process(uuid, args);
      if (optional.isPresent()) {
        args = optional.get();
      } else {
        return Optional.empty();
      }
    }
    return Optional.of(args);
  }

  @Override
  public String getValue(String query, UUID uuid) {
    for (Map.Entry<String, ArgumentProcessor> entry : processorMap.entrySet()) {
      String key = entry.getKey();
      if (query.toLowerCase(Locale.ROOT).startsWith(key.toLowerCase(Locale.ROOT))) {
        String subQuery = query.substring(key.length());
        if (subQuery.startsWith("_")) {
          subQuery = subQuery.substring(1);
        }
        return entry.getValue().getValue(subQuery, uuid);
      }
    }
    return "";
  }

  @Override
  public Pair<Optional<List<String>>, String[]> tabComplete(UUID uuid, String[] args) {
    for (ArgumentProcessor processor : processorMap.values()) {
      Pair<Optional<List<String>>, String[]> pair = processor.tabComplete(uuid, args);
      Optional<List<String>> optional = pair.getKey();
      if (optional.isPresent()) {
        return pair;
      } else {
        args = pair.getValue();
      }
    }
    return Pair.of(Optional.empty(), args);
  }

  /**
   * Get the tab complete for the arguments
   *
   * @param uuid the UUID of the player
   * @param args the arguments
   *
   * @return the suggestions
   */
  public List<String> handleTabComplete(UUID uuid, String[] args) {
    return tabComplete(uuid, args).getKey().orElse(Collections.emptyList());
  }

  @Override
  public Menu getMenu() {
    return menu;
  }
}
