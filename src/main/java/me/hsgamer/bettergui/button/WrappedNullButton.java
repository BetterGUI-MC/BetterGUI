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
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.button.impl.NullButton;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import org.bukkit.Bukkit;

import java.util.*;

public class WrappedNullButton extends BaseWrappedButton {

  private final Map<AdvancedClickType, List<Action>> actionMap = new HashMap<>();

  /**
   * Create a new button
   *
   * @param menu the menu
   */
  public WrappedNullButton(Menu menu) {
    super(menu);
  }

  private void setActions(Object o) {
    Map<String, AdvancedClickType> clickTypeMap = ClickTypeUtils.getClickTypeMap();
    if (o instanceof Map) {
      // noinspection unchecked
      Map<String, Object> keys = new CaseInsensitiveStringHashMap<>((Map<String, Object>) o);
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
  protected Button createButton(Map<String, Object> section) {
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section);
    boolean closeOnClick = Optional.ofNullable(keys.get("close-on-click")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false);
    Optional.ofNullable(keys.get("command")).ifPresent(this::setActions);
    Optional.ofNullable(keys.get("action")).ifPresent(this::setActions);

    return new NullButton((uuid, event) -> {
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
