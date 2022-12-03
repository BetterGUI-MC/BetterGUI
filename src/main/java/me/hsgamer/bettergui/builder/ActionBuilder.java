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
    register(SoundAction::new, "sound", "raw-sound");
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
        String action = input.type;
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

        String type;
        String value;
        if (split.length > 1) {
          type = split[0];
          value = split[1];
          value = value.startsWith(" ") ? value.substring(1) : value;
        } else {
          type = split[0];
          value = "";
        }
        type = type.trim();

        return Stream.of(
          build(new Input(menu, type, value))
            .orElse(new PlayerAction(new Input(menu, "player", split[0])))
        );
      })
      .collect(Collectors.toList());
  }

  /**
   * The input for the action builder
   */
  public static class Input {
    public final Menu menu;
    public final String type;
    public final String value;

    /**
     * Create a new input
     *
     * @param menu  the menu
     * @param type  the type of the action
     * @param value the value of the action
     */
    public Input(Menu menu, String type, String value) {
      this.menu = menu;
      this.type = type;
      this.value = value;
    }
  }
}
