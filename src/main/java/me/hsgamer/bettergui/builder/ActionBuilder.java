package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.builder.MassBuilder;
import me.hsgamer.hscore.common.CollectionUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The action builder
 */
public final class ActionBuilder extends MassBuilder<ActionBuilder.Input, Action> {
  /**
   * The instance of the action builder
   */
  public static final ActionBuilder INSTANCE = new ActionBuilder();

  private ActionBuilder() {
  }

  /**
   * Register a new action creator
   *
   * @param creator the creator
   * @param type    the type
   */
  public void register(Function<Input, Action> creator, String... type) {
    register(new Element<Input, Action>() {
      @Override
      public boolean canBuild(Input input) {
        String action = input.name;
        for (String s : type) {
          if (action.equalsIgnoreCase(s)) {
            return true;
          }
        }
        return false;
      }

      @Override
      public Action build(Input input) {
        return creator.apply(input);
      }
    });
  }

  /**
   * Build a list of actions
   *
   * @param menu   the menu involved in
   * @param object the object
   *
   * @return the list of actions
   */
  public List<Action> build(Menu menu, Object object) {
    return CollectionUtils.createStringListFromObject(object, true)
      .stream()
      .flatMap(string -> {
        String[] split = string.split(":", 2);
        String name = split[0];
        String value = split.length > 1 ? split[1] : "";
        return build(new Input(menu, name, value)).map(Stream::of).orElseGet(Stream::empty);
      })
      .collect(Collectors.toList());
  }

  public static class Input {
    public final Menu menu;
    public final String name;
    public final String value;

    public Input(Menu menu, String name, String value) {
      this.menu = menu;
      this.name = name;
      this.value = value;
    }
  }
}
