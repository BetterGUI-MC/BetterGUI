package me.hsgamer.bettergui.argument.type;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.argument.ArgumentProcessor;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ArgumentProcessorBuilder;
import me.hsgamer.hscore.common.MapUtils;

import java.util.Collections;
import java.util.Map;

public abstract class BaseActionArgumentProcessor implements ArgumentProcessor {
  protected final Map<String, Object> options;
  protected final ActionApplier onRequiredActionApplier;
  protected final ActionApplier onInvalidActionApplier;
  private final ArgumentProcessorBuilder.Input input;

  public BaseActionArgumentProcessor(ArgumentProcessorBuilder.Input input) {
    this.input = input;
    options = MapUtils.createLowercaseStringObjectMap(input.options);
    this.onRequiredActionApplier = new ActionApplier(input.menu, MapUtils.getIfFoundOrDefault(options, Collections.emptyList(), "required-command", "required-action", "action", "command"));
    this.onInvalidActionApplier = new ActionApplier(input.menu, MapUtils.getIfFoundOrDefault(options, Collections.emptyList(), "invalid-command", "invalid-action"));
  }

  @Override
  public Menu getMenu() {
    return input.menu;
  }
}
