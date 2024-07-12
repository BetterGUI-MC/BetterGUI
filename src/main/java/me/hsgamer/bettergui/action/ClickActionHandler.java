package me.hsgamer.bettergui.action;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.bukkit.clicktype.BukkitClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A handler to handle the click action
 */
public class ClickActionHandler {
  private final Menu menu;
  private final Map<BukkitClickType, ActionApplier> actionMap;
  private final boolean closeOnClick;

  /**
   * Create a new click action handler
   *
   * @param menu         the menu
   * @param actionMap    the action map
   * @param closeOnClick if the menu should close when the player click
   */
  public ClickActionHandler(Menu menu, Map<BukkitClickType, ActionApplier> actionMap, boolean closeOnClick) {
    this.menu = menu;
    this.actionMap = actionMap;
    this.closeOnClick = closeOnClick;
  }

  /**
   * Create a new click action handler
   *
   * @param menu         the menu
   * @param o            the action value
   * @param closeOnClick if the menu should close when the player click
   */
  public ClickActionHandler(Menu menu, Object o, boolean closeOnClick) {
    this(menu, new HashMap<>(), closeOnClick);
    Map<String, BukkitClickType> clickTypeMap = ClickTypeUtils.getClickTypeMap();
    if (o instanceof Map) {
      // noinspection unchecked
      Map<String, Object> keys = new CaseInsensitiveStringMap<>((Map<String, Object>) o);
      Optional<ActionApplier> defaultActionApplier = Optional.ofNullable(keys.get("default")).map(value -> new ActionApplier(menu, value));
      clickTypeMap.forEach((clickTypeName, clickType) -> {
        if (keys.containsKey(clickTypeName)) {
          actionMap.put(clickType, new ActionApplier(menu, keys.get(clickTypeName)));
        } else {
          defaultActionApplier.ifPresent(actionApplier -> actionMap.put(clickType, actionApplier));
        }
      });
    } else {
      clickTypeMap.values().forEach(advancedClickType -> actionMap.put(advancedClickType, new ActionApplier(menu, o)));
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
                  menu.close(player);
                } finally {
                  process.next();
                }
              }, process::next)
          )
        );
    }
  }
}
