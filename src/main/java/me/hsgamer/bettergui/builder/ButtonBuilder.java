package me.hsgamer.bettergui.builder;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.MenuButton;
import me.hsgamer.bettergui.api.element.MenuElement;
import me.hsgamer.bettergui.button.*;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.hscore.common.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The button builder
 */
public final class ButtonBuilder extends FunctionalMassBuilder<ButtonBuilder.Input, MenuButton> implements Loadable {
  public ButtonBuilder() {
  }

  @Override
  public void load() {
    register(TemplateButton::new, "template");
    register(WrappedDummyButton::new, "dummy", "raw");
    register(EmptyButton::new, "empty");
    register(WrappedAirButton::new, "air");
    register(WrappedPredicateButton::new, "predicate", "requirement");
    register(WrappedListButton::new, "list");
    register(WrappedAnimatedButton::new, "animated", "animate", "anim");
    register(WrappedNullButton::new, "null", "none");
    register(input ->
        BetterGUI.getInstance().get(MainConfig.class).isUseLegacyButton()
          ? new LegacyMenuButton(input)
          : new WrappedSimpleButton(input),
      "simple"
    );
  }

  @Override
  public void disable() {
    clear();
  }

  @Override
  protected String getType(Input input) {
    return Objects.toString(MapUtils.createLowercaseStringObjectMap(input.options).get("type"), "simple");
  }

  /**
   * Get the child buttons from the parent element
   *
   * @param parent  the parent element
   * @param section the child section
   *
   * @return the child buttons
   */
  public List<MenuButton> getChildButtons(MenuElement parent, Map<String, Object> section) {
    return section.entrySet()
      .stream()
      .filter(entry -> entry.getValue() instanceof Map)
      .map(entry -> {
        // noinspection unchecked
        Map<String, Object> value = (Map<String, Object>) entry.getValue();
        return new Input(parent, entry.getKey(), value);
      })
      .flatMap(input -> build(input).map(Stream::of).orElseGet(Stream::empty))
      .collect(Collectors.toList());
  }

  /**
   * The input for the button builder
   */
  public static class Input {
    public final MenuElement parent;
    public final String name;
    public final Map<String, Object> options;

    /**
     * Create a new input
     *
     * @param parent  the parent element
     * @param name    the name of the button
     * @param options the options of the button
     */
    public Input(MenuElement parent, String name, Map<String, Object> options) {
      this.parent = parent;
      this.name = name;
      this.options = options;
    }
  }
}
