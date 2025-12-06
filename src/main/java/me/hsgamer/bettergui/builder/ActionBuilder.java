package me.hsgamer.bettergui.builder;

import io.github.projectunified.minelib.plugin.base.Loadable;
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
public final class ActionBuilder extends me.hsgamer.hscore.action.builder.ActionBuilder<ActionBuilder.Input> implements Loadable {
  private final BetterGUI plugin;

  public ActionBuilder(BetterGUI plugin) {
    this.plugin = plugin;
  }

  @Override
  public void load() {
    BukkitActionBuilder.register(this, plugin);
    register(OpenMenuAction::new, "open-menu", "open", "menu", "open-menu");
    register(BackAction::new, "back-menu", "backmenu");
    register(input -> new CloseMenuAction(input.getMenu()), "close-menu", "closemenu");
    register(input -> new UpdateMenuAction(input.getMenu()), "update-menu", "updatemenu");
    register(input -> new SoundAction(input.getValue()), "sound", "raw-sound");
  }

  @Override
  public void disable() {
    clear();
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
    static ActionInput create(String input) {
      input = input.trim();

      // Find the colon to separate type/option from value
      int colonIndex = input.indexOf(':');
      String typeOptionPart = colonIndex == -1 ? input : input.substring(0, colonIndex);
      String value = colonIndex == -1 ? "" : input.substring(colonIndex + 1).trim();

      typeOptionPart = typeOptionPart.trim();

      // Find the opening parenthesis to separate type from option
      int openParenIndex = typeOptionPart.indexOf('(');
      String type;
      String option = "";

      if (openParenIndex == -1) {
        type = typeOptionPart;
      } else {
        type = typeOptionPart.substring(0, openParenIndex).trim();
        int closeParenIndex = typeOptionPart.lastIndexOf(')');
        if (closeParenIndex > openParenIndex) {
          option = typeOptionPart.substring(openParenIndex + 1, closeParenIndex).trim();
        }
      }

      // If no type is found, use the entire input as value
      if (type.isEmpty() && value.isEmpty()) {
        return ActionInput.create("", "", input);
      }

      return ActionInput.create(type, option, value);
    }

    static Input create(Menu menu, String input) {
      ActionInput actionInput = create(input);
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
