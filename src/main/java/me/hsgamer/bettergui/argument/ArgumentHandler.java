package me.hsgamer.bettergui.argument;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.argument.ArgumentProcessor;
import me.hsgamer.bettergui.api.element.MenuElement;
import me.hsgamer.bettergui.api.element.WithElementLookupStringReplacer;
import me.hsgamer.bettergui.builder.ArgumentProcessorBuilder;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;

import java.util.*;

/**
 * The handler for arguments
 */
public class ArgumentHandler implements ArgumentProcessor, WithElementLookupStringReplacer<ArgumentProcessor> {
  private final MenuElement menuElement;
  private final List<ArgumentProcessor> processors = new ArrayList<>();

  /**
   * Create a new handler
   *
   * @param menuElement the menu element
   */
  public ArgumentHandler(MenuElement menuElement, Map<String, Object> section) {
    this.menuElement = menuElement;
    Map<String, Object> keys = MapUtils.createLowercaseStringObjectMap(section);
    keys.forEach((key, value) -> {
      if (value instanceof Map) {
        //noinspection unchecked
        Map<String, Object> map = (Map<String, Object>) value;
        BetterGUI.getInstance().get(ArgumentProcessorBuilder.class)
          .build(new ArgumentProcessorBuilder.Input(menuElement, key, map))
          .ifPresent(processors::add);
      }
    });
  }

  @Override
  public Optional<String[]> process(UUID uuid, String[] args) {
    for (ArgumentProcessor processor : processors) {
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
  public Pair<Optional<List<String>>, String[]> tabComplete(UUID uuid, String[] args) {
    for (ArgumentProcessor processor : processors) {
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
  public MenuElement getParent() {
    return menuElement;
  }

  @Override
  public String getName() {
    return "argument_handler";
  }

  @Override
  public List<ArgumentProcessor> getElements() {
    return processors;
  }
}
