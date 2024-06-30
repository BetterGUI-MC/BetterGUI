package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.api.argument.ArgumentProcessor;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.argument.type.*;
import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.Map;
import java.util.Objects;

/**
 * The argument processor builder
 */
public final class ArgumentProcessorBuilder extends FunctionalMassBuilder<ArgumentProcessorBuilder.Input, ArgumentProcessor> {
  /**
   * The instance of the argument processor builder
   */
  public static final ArgumentProcessorBuilder INSTANCE = new ArgumentProcessorBuilder();

  private ArgumentProcessorBuilder() {
    register(StoreArgumentProcessor::new, "store");
    register(PlayerArgumentProcessor::new, "player");
    register(NumberArgumentProcessor::new, "number", "int", "integer", "long");
    register(DecimalArgumentProcessor::new, "decimal", "float", "double");
    register(EntityTypeArgumentProcessor::new, "entity");
    register(MaterialArgumentProcessor::new, "material", "item");
  }

  @Override
  protected String getType(Input input) {
    return Objects.toString(new CaseInsensitiveStringMap<>(input.options).get("type"), "store");
  }

  /**
   * The input for the argument processor builder
   */
  public static class Input {
    public final Menu menu;
    public final String name;
    public final Map<String, Object> options;

    /**
     * Create a new input
     *
     * @param menu    the menu
     * @param name    the name of the button
     * @param options the options of the button
     */
    public Input(Menu menu, String name, Map<String, Object> options) {
      this.menu = menu;
      this.name = name;
      this.options = options;
    }
  }
}
