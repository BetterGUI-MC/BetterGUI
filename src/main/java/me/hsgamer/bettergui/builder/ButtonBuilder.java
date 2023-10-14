package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.button.*;
import me.hsgamer.hscore.builder.MassBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The button builder
 */
public final class ButtonBuilder extends MassBuilder<ButtonBuilder.Input, WrappedButton> {
  /**
   * The instance of the button builder
   */
  public static final ButtonBuilder INSTANCE = new ButtonBuilder();

  private ButtonBuilder() {
    register(TemplateButton::new, "template");
    register(WrappedDummyButton::new, "dummy", "raw");
    register(EmptyButton::new, "empty");
    register(WrappedAirButton::new, "air");
    register(WrappedPredicateButton::new, "predicate", "requirement");
    register(WrappedListButton::new, "list");
    register(WrappedAnimatedButton::new, "animated", "animate", "anim");
    register(WrappedNullButton::new, "null", "none");
    register(input ->
        BetterGUI.getInstance().getMainConfig().isUseLegacyButton()
          ? new LegacyMenuButton(input)
          : new WrappedSimpleButton(input),
      "simple"
    );
  }

  /**
   * Register a new button creator
   *
   * @param creator the creator
   * @param type    the type
   */
  public void register(Function<Input, WrappedButton> creator, String... type) {
    register(input -> {
      Map<String, Object> keys = new CaseInsensitiveStringMap<>(input.options);
      String button = Objects.toString(keys.get("type"), "simple");
      for (String s : type) {
        if (button.equalsIgnoreCase(s)) {
          return Optional.of(creator.apply(input));
        }
      }
      return Optional.empty();
    });
  }

  /**
   * Get the child buttons from the parent button
   *
   * @param parentButton the parent button
   * @param section      the child section
   *
   * @return the child buttons
   */
  public List<WrappedButton> getChildButtons(WrappedButton parentButton, Map<String, Object> section) {
    return section.entrySet()
      .stream()
      .filter(entry -> entry.getValue() instanceof Map)
      .map(entry -> {
        // noinspection unchecked
        Map<String, Object> value = (Map<String, Object>) entry.getValue();
        String name = parentButton.getName() + "_child_" + entry.getKey();
        return new Input(parentButton.getMenu(), name, value);
      })
      .flatMap(input -> build(input).map(Stream::of).orElseGet(Stream::empty))
      .collect(Collectors.toList());
  }

  /**
   * The input for the button builder
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
