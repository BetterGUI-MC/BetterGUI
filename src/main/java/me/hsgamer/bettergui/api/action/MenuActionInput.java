package me.hsgamer.bettergui.api.action;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.hscore.action.builder.ActionInput;

public interface MenuActionInput extends ActionInput, MenuElement {
  static MenuActionInput create(Menu menu, ActionInput input) {
    return new MenuActionInput() {
      @Override
      public String getType() {
        return input.getType();
      }

      @Override
      public String getOption() {
        return input.getOption();
      }

      @Override
      public String getValue() {
        return input.getValue();
      }

      @Override
      public Menu getMenu() {
        return menu;
      }
    };
  }

  static MenuActionInput create(MenuElement menuElement, ActionInput input) {
    return create(menuElement.getMenu(), input);
  }
}
