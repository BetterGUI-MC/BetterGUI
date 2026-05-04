package me.hsgamer.bettergui.action;

import me.hsgamer.bettergui.api.element.MenuElement;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.bukkit.clicktype.BukkitClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.Bukkit;

import java.util.*;

/**
 * A handler to handle the click action
 */
public class ClickActionHandler {
  private final MenuElement menuElement;
  private final Map<BukkitClickType, ActionApplier> actionMap;
  private final boolean closeOnClick;

  /**
   * Create a new click action handler
   *
   * @param menuElement  the menu element
   * @param actionMap    the action map
   * @param closeOnClick if the menu should close when the player click
   */
  public ClickActionHandler(MenuElement menuElement, Map<BukkitClickType, ActionApplier> actionMap, boolean closeOnClick) {
    this.menuElement = menuElement;
    this.actionMap = actionMap;
    this.closeOnClick = closeOnClick;
  }

  /**
   * Create a new click action handler
   *
   * @param menuElement  the menu element
   * @param o            the action value
   * @param closeOnClick if the menu should close when the player click
   */
  public ClickActionHandler(MenuElement menuElement, Object o, boolean closeOnClick) {
    this(menuElement, new HashMap<>(), closeOnClick);
    Map<String, BukkitClickType> clickTypeMap = ClickTypeUtils.getClickTypeMap();
    if (o instanceof Map) {
      Map<String, Object> keys = MapUtils.createLowercaseStringObjectMap((Map<?, ?>) o);
      Optional<ActionApplier> defaultActionApplier = Optional.ofNullable(keys.get("default")).map(value -> new ActionApplier(menuElement, value));
      clickTypeMap.forEach((clickTypeName, clickType) -> {
        Object value = keys.get(clickTypeName.toLowerCase(Locale.ROOT));
        if (value != null) {
          actionMap.put(clickType, new ActionApplier(menuElement, value));
        } else {
          defaultActionApplier.ifPresent(actionApplier -> actionMap.put(clickType, actionApplier));
        }
      });
    } else {
      clickTypeMap.values().forEach(advancedClickType -> actionMap.put(advancedClickType, new ActionApplier(menuElement, o)));
    }
  }

  /**
   * Apply the action to the runnable
   *
   * @param uuid          the unique id
   * @param clickType     the click type
   * @param batchRunnable the batch runnable
   */
  public void apply(UUID uuid, BukkitClickType clickType, BatchRunnable batchRunnable) {
    Optional.ofNullable(actionMap.get(clickType))
      .ifPresent(actionApplier ->
        batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> actionApplier.accept(uuid, process))
      );
    if (closeOnClick) {
      Optional.ofNullable(Bukkit.getPlayer(uuid))
        .ifPresent(player ->
          batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process ->
            SchedulerUtil.entity(player)
              .run(() -> {
                try {
                  menuElement.getMenu().close(player);
                } finally {
                  process.next();
                }
              }, process::next)
          )
        );
    }
  }
}
