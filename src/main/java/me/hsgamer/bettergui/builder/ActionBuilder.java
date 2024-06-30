package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.action.type.*;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.menu.MenuElement;
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
public final class ActionBuilder extends me.hsgamer.hscore.action.builder.ActionBuilder<ActionBuilder.Input> {
  /**
   * The instance of the action builder
   */
  public static final ActionBuilder INSTANCE = new ActionBuilder();

  private ActionBuilder() {
    BukkitActionBuilder.register(this, BetterGUI.getInstance());
    register(OpenMenuAction::new, "open-menu", "open", "menu", "open-menu");
    register(input -> new BackAction(input.getMenu()), "back-menu", "backmenu");
    register(input -> new CloseMenuAction(input.getMenu()), "close-menu", "closemenu");
    register(input -> new UpdateMenuAction(input.getMenu()), "update-menu", "updatemenu");
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
    List<Input> inputs = CollectionUtils.createStringListFromObject(object, true)
      .stream()
      .map(input -> Input.create(menu, input))
      .collect(Collectors.toList());
    return build(inputs, input -> new PlayerAction(BetterGUI.getInstance(), input.getOriginalValue()));
  }

  public interface Input extends ActionInput, MenuElement {
    static Input create(Menu menu, String input) {
      ActionInput actionInput = ActionInput.create(input);
      return new Input() {
        @Override
        public String getType() {
          return actionInput.getType();
        }

        @Override
        public String getOption() {
          return actionInput.getOption();
        }

        @Override
        public String getValue() {
          return actionInput.getValue();
        }

        @Override
        public Menu getMenu() {
          return menu;
        }

        @Override
        public String getOriginalValue() {
          return input;
        }
      };
    }

    static Input create(MenuElement menuElement, String input) {
      return create(menuElement.getMenu(), input);
    }

    String getOriginalValue();
  }
}
