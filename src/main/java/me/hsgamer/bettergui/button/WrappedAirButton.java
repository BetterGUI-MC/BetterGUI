package me.hsgamer.bettergui.button;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.utils.ButtonUtils;
import me.hsgamer.hscore.bukkit.clicktype.AdvancedClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.button.impl.AirButton;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

  @Override
  protected Button createButton(Map<String, Object> section) {
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section);
    boolean closeOnClick = Optional.ofNullable(keys.get("close-on-click")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false);
    Optional.ofNullable(keys.get("command")).map(o -> ButtonUtils.convertActions(o, this)).ifPresent(actionMap::putAll);
    Optional.ofNullable(keys.get("action")).map(o -> ButtonUtils.convertActions(o, this)).ifPresent(actionMap::putAll);

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
