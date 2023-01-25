package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.api.argument.ArgumentProcessor;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.builder.Builder;

/**
 * The argument processor builder
 */
public final class ArgumentProcessorBuilder extends Builder<Menu, ArgumentProcessor> {
  /**
   * The instance of the argument processor builder
   */
  public static final ArgumentProcessorBuilder INSTANCE = new ArgumentProcessorBuilder();

  private ArgumentProcessorBuilder() {

  }
}
