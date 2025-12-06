package me.hsgamer.bettergui.button;

import io.github.projectunified.craftux.common.Button;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.action.ClickActionHandler;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.bukkit.clicktype.BukkitClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ActionButton<B extends Button> extends BaseWrappedButton<B> {
  protected ActionButton(ButtonBuilder.Input input) {
    super(input);
  }

  protected abstract Function<Consumer<InventoryClickEvent>, B> getButtonFunction(Map<String, Object> section);

  @Override
  protected B createButton(Map<String, Object> section) {
    Map<String, Object> keys = MapUtils.createLowercaseStringObjectMap(section);
    ClickActionHandler clickActionHandler = new ClickActionHandler(
      menu,
      MapUtils.getIfFoundOrDefault(keys, Collections.emptyList(), "command", "action"),
      Optional.ofNullable(keys.get("close-on-click")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false)
    );
    return getButtonFunction(section).apply(clickEvent -> {
      BukkitClickType clickType = ClickTypeUtils.getClickTypeFromEvent(clickEvent, BetterGUI.getInstance().get(MainConfig.class).isModernClickType());
      BatchRunnable batchRunnable = new BatchRunnable();
      clickActionHandler.apply(clickEvent.getWhoClicked().getUniqueId(), clickType, batchRunnable);
      SchedulerUtil.async().run(batchRunnable);
    });
  }
}
