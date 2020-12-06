package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.action.*;
import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.builder.Builder;
import me.hsgamer.hscore.common.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The action builder
 */
public class ActionBuilder extends Builder<String, Action> {

  /**
   * The instance of the action builder
   */
  public static final ActionBuilder INSTANCE = new ActionBuilder();

  private ActionBuilder() {
    registerDefaultActions();
  }

  private void registerDefaultActions() {
    register(ConsoleAction::new, "console");
    register(OpAction::new, "op");
    register(PlayerAction::new, "player");
    register(DelayAction::new, "delay");
    register(ConditionAction::new, "condition");
    register(OpenMenuAction::new, "open-menu", "open", "menu", "open-menu");
    register(s -> new BackAction(), "back-menu", "backmenu");
    register(TellAction::new, "tell");
    register(BroadcastAction::new, "broadcast");
    register(s -> new CloseMenuAction(), "close-menu", "closemenu");
    register(s -> new UpdateMenuAction(), "update-menu", "updatemenu");
    register(PermissionAction::new, "permission");
    register(SoundAction::new, "sound");
    register(RawSoundAction::new, "raw-sound");
    register(MusicAction::new, "music");
  }

  /**
   * Build a list of actions
   *
   * @param menu   the menu involved in
   * @param object the object
   *
   * @return the list of actions
   */
  public List<Action> getActions(Menu menu, Object object) {
    return CollectionUtils.createStringListFromObject(object, true)
      .stream()
      .map(string -> {
        String[] split = string.split(":", 2);
        String name = split[0];
        String value = split.length > 1 ? split[1] : "";

        Action action = build(name.trim(), value.trim()).orElseGet(() -> new PlayerAction(string.trim()));
        action.setMenu(menu);
        return action;
      })
      .collect(Collectors.toList());
  }
}
