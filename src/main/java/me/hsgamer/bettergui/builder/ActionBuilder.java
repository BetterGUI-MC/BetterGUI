package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.action.type.*;
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
    register(ConsoleAction::new, "console");
    register(OpAction::new, "op");
    register(PlayerAction::new, "player");
    register(DelayAction::new, "delay");
    register(OpenMenuAction::new, "open-menu", "open", "menu", "open-menu");
    register(input -> new BackAction(input.menu), "back-menu", "backmenu");
    register(TellAction::new, "tell");
    register(BroadcastAction::new, "broadcast");
    register(input -> new CloseMenuAction(input.menu), "close-menu", "closemenu");
    register(input -> new UpdateMenuAction(input.menu), "update-menu", "updatemenu");
    register(PermissionAction::new, "permission");
    register(SoundAction::new, "sound");
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
        String name;
        String value;
        if (split.length > 1) {
          name = split[0];
          value = split[1];
          value = value.startsWith(" ") ? value.substring(1) : value;
        } else {
          name = "player";
          value = split[0];
        }
        return build(new Input(menu, name.trim(), value)).map(Stream::of).orElseGet(Stream::empty);
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
