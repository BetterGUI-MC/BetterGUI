package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.api.argument.ArgumentProcessor;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.argument.type.PlayerArgumentProcessor;
import me.hsgamer.bettergui.argument.type.StoreArgumentProcessor;
import me.hsgamer.hscore.builder.MassBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * The argument processor builder
 */
public final class ArgumentProcessorBuilder extends MassBuilder<ArgumentProcessorBuilder.Input, ArgumentProcessor> {
  /**
   * The instance of the argument processor builder
   */
  public static final ArgumentProcessorBuilder INSTANCE = new ArgumentProcessorBuilder();

  private ArgumentProcessorBuilder() {
    register(StoreArgumentProcessor::new, "store");
    register(PlayerArgumentProcessor::new, "player");
  }

  /**
   * Register a new processor creator
   *
   * @param creator the creator
   * @param type    the type
   */
  public void register(Function<ArgumentProcessorBuilder.Input, ArgumentProcessor> creator, String... type) {
    register(input -> {
      Map<String, Object> keys = new CaseInsensitiveStringMap<>(input.options);
      String processor = Objects.toString(keys.get("type"), "store");
      for (String s : type) {
        if (processor.equalsIgnoreCase(s)) {
          return Optional.of(creator.apply(input));
        }
      }
      return Optional.empty();
    });
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
