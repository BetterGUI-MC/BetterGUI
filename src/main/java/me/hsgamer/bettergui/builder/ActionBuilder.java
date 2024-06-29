package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.action.type.*;
import me.hsgamer.bettergui.api.action.MenuActionInput;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.action.builder.ActionInput;
import me.hsgamer.hscore.action.common.Action;
import me.hsgamer.hscore.bukkit.action.PlayerAction;
import me.hsgamer.hscore.bukkit.action.builder.BukkitActionBuilder;
import me.hsgamer.hscore.common.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The action builder
 */
public final class ActionBuilder extends me.hsgamer.hscore.action.builder.ActionBuilder {
  /**
   * The instance of the action builder
   */
  public static final ActionBuilder INSTANCE = new ActionBuilder();

  private ActionBuilder() {
    BukkitActionBuilder.register(this, BetterGUI.getInstance());
    register(input -> new OpenMenuAction((MenuActionInput) input), "open-menu", "open", "menu", "open-menu");
    register(input -> new BackAction(((MenuActionInput) input).getMenu()), "back-menu", "backmenu");
    register(input -> new CloseMenuAction(((MenuActionInput) input).getMenu()), "close-menu", "closemenu");
    register(input -> new UpdateMenuAction(((MenuActionInput) input).getMenu()), "update-menu", "updatemenu");
    register(input -> new SoundAction(input.getValue()), "sound", "raw-sound");
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
    List<ActionInput> inputs = CollectionUtils.createStringListFromObject(object, true)
      .stream()
      .map(ActionInput::create)
      .map(input -> MenuActionInput.create(menu, input))
      .collect(Collectors.toList());
    return build(inputs, input -> new PlayerAction(BetterGUI.getInstance(), input.getValue()));
  }
}
