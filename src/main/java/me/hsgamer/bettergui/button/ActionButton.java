package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.action.ClickActionHandler;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.bukkit.clicktype.AdvancedClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class ActionButton extends BaseWrappedButton {
  public ActionButton(ButtonBuilder.Input input) {
    super(input);
  }

  protected abstract Function<BiConsumer<UUID, InventoryClickEvent>, Button> getButtonFunction(Map<String, Object> section);

  @Override
  protected Button createButton(Map<String, Object> section) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);
    ClickActionHandler clickActionHandler = new ClickActionHandler(
      menu,
      MapUtil.getIfFoundOrDefault(keys, Collections.emptyList(), "command", "action"),
      Optional.ofNullable(keys.get("close-on-click")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false)
    );
    return getButtonFunction(section).apply((uuid, event) -> {
      AdvancedClickType clickType = ClickTypeUtils.getClickTypeFromEvent(event, BetterGUI.getInstance().getMainConfig().modernClickType);
      BatchRunnable batchRunnable = new BatchRunnable();
      clickActionHandler.apply(uuid, clickType, batchRunnable);
      Bukkit.getScheduler().runTaskAsynchronously(BetterGUI.getInstance(), batchRunnable);
    });
  }
}
