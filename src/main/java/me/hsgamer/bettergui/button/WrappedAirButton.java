package me.hsgamer.bettergui.button;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.hscore.bukkit.clicktype.AdvancedClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.bukkit.gui.Button;
import me.hsgamer.hscore.bukkit.gui.button.AirButton;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import org.bukkit.Bukkit;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.*;

public class WrappedAirButton extends BaseWrappedButton {

  private final Map<AdvancedClickType, List<Action>> actionMap = new HashMap<>();

  /**
   * Create a new button
   *
   * @param menu the menu
   */
  public WrappedAirButton(Menu menu) {
    super(menu);
  }

  private void setActions(Object o) {
    Map<String, AdvancedClickType> clickTypeMap = ClickTypeUtils.getClickTypeMap();
    if (o instanceof ConfigurationSection) {
      Map<String, Object> keys = new CaseInsensitiveStringMap<>(((ConfigurationSection) o).getValues(false));
      List<Action> defaultActions = Optional.ofNullable(keys.get("default")).map(value -> ActionBuilder.INSTANCE.getActions(getMenu(), value)).orElse(Collections.emptyList());
      clickTypeMap.forEach((clickTypeName, clickType) -> {
        if (keys.containsKey(clickTypeName)) {
          actionMap.put(clickType, ActionBuilder.INSTANCE.getActions(getMenu(), keys.get(clickTypeName)));
        } else {
          actionMap.put(clickType, defaultActions);
        }
      });
    } else {
      clickTypeMap.values().forEach(advancedClickType -> actionMap.put(advancedClickType, ActionBuilder.INSTANCE.getActions(getMenu(), o)));
    }
  }

  @Override
  protected Button createButton(ConfigurationSection section) {
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section.getValues(false));
    boolean closeOnClick = Optional.ofNullable(keys.get("close-on-click")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false);
    Optional.ofNullable(keys.get("command")).ifPresent(this::setActions);
    Optional.ofNullable(keys.get("action")).ifPresent(this::setActions);

    return new AirButton((uuid, event) -> {
      TaskChain<?> taskChain = BetterGUI.newChain();
      if (closeOnClick) {
        Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> taskChain.sync(() -> getMenu().closeInventory(player)));
      }
      Optional
        .ofNullable(actionMap.get(ClickTypeUtils.getClickTypeFromEvent(event, Boolean.TRUE.equals(MainConfig.MODERN_CLICK_TYPE.getValue()))))
        .ifPresent(actions -> actions.forEach(action -> action.addToTaskChain(uuid, taskChain)));
      taskChain.execute();
    });
  }
}
