package me.hsgamer.bettergui.argument;

import me.hsgamer.bettergui.api.argument.ArgumentProcessor;
import me.hsgamer.bettergui.api.menu.Menu;

import java.util.*;

/**
 * The handler for arguments
 */
public class ArgumentHandler implements ArgumentProcessor {
  private final Menu menu;
  private final List<ArgumentProcessor> processors = new ArrayList<>();

  /**
   * Create a new handler
   *
   * @param menu the menu
   */
  public ArgumentHandler(Menu menu) {
    this.menu = menu;
  }

  /**
   * Add a processor
   *
   * @param processor the processor
   */
  public void addProcessor(ArgumentProcessor processor) {
    processors.add(processor);
  }

  /**
   * Remove a processor
   *
   * @param processor the processor
   */
  public void removeProcessor(ArgumentProcessor processor) {
    processors.remove(processor);
  }

  /**
   * Clear all processors
   */
  public void clearProcessors() {
    onClearAll();
    processors.clear();
  }

  /**
   * Get the processors
   *
   * @return the processors
   */
  public List<ArgumentProcessor> getProcessors() {
    return Collections.unmodifiableList(processors);
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
  public void onClear(UUID uuid) {
    for (ArgumentProcessor processor : processors) {
      processor.onClear(uuid);
    }
  }

  @Override
  public void onClearAll() {
    for (ArgumentProcessor processor : processors) {
      processor.onClearAll();
    }
  }

  @Override
  public Menu getMenu() {
    return menu;
  }
}
