package me.hsgamer.bettergui.builder;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.action.type.BackAction;
import me.hsgamer.bettergui.action.type.CloseMenuAction;
import me.hsgamer.bettergui.action.type.OpenMenuAction;
import me.hsgamer.bettergui.action.type.UpdateMenuAction;
import me.hsgamer.bettergui.api.element.MenuElement;
import me.hsgamer.hscore.action.builder.ActionInput;
import me.hsgamer.hscore.action.common.Action;
import me.hsgamer.hscore.bukkit.action.PlayerAction;
import me.hsgamer.hscore.bukkit.action.builder.BukkitActionBuilder;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import org.bukkit.plugin.java.JavaPlugin;

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
    BukkitActionBuilder.register(this, plugin, ColorUtils::colorize);
    register(input -> new OpenMenuAction(plugin, input), "open-menu", "open", "menu", "open-menu");
    register(input -> new BackAction(plugin, input), "back-menu", "backmenu");
    register(input -> new CloseMenuAction(input.getMenuElement().getMenu()), "close-menu", "closemenu");
    register(input -> new UpdateMenuAction(input.getMenuElement().getMenu()), "update-menu", "updatemenu");
  }

  @Override
  public void disable() {
    clear();
  }

  /**
   * Build a list of actions
   *
   * @param menuElement the menu element involved in
   * @param object      the object
   *
   * @return the list of actions
   */
  public List<Action> build(MenuElement menuElement, Object object) {
    List<Input> inputs = CollectionUtils.createStringListFromObject(object, true)
      .stream()
      .map(input -> Input.create(menuElement, input))
      .collect(Collectors.toList());
    return build(inputs, input -> new PlayerAction(JavaPlugin.getPlugin(BetterGUI.class), input.getOriginalValue()));
  }

  public interface Input extends ActionInput {
    static Input create(MenuElement element, String input) {
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
        public MenuElement getMenuElement() {
          return element;
        }

        @Override
        public String getOriginalValue() {
          return input;
        }
      };
    }

    MenuElement getMenuElement();

    String getOriginalValue();
  }
}
