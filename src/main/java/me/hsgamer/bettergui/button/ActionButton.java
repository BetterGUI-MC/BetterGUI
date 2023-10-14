package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.action.ClickActionHandler;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.bukkit.clicktype.AdvancedClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.bukkit.gui.event.BukkitClickEvent;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.minecraft.gui.button.Button;
import me.hsgamer.hscore.minecraft.gui.event.ClickEvent;
import me.hsgamer.hscore.task.BatchRunnable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ActionButton<B extends Button> extends BaseWrappedButton<B> {
  protected ActionButton(ButtonBuilder.Input input) {
    super(input);
  }

  protected abstract Function<Consumer<ClickEvent>, B> getButtonFunction(Map<String, Object> section);

  @Override
  protected B createButton(Map<String, Object> section) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);
    ClickActionHandler clickActionHandler = new ClickActionHandler(
      menu,
      MapUtil.getIfFoundOrDefault(keys, Collections.emptyList(), "command", "action"),
      Optional.ofNullable(keys.get("close-on-click")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false)
    );
    return getButtonFunction(section).apply(clickEvent -> {
      if (!(clickEvent instanceof BukkitClickEvent)) return;
      BukkitClickEvent bukkitClickEvent = (BukkitClickEvent) clickEvent;
      AdvancedClickType clickType = ClickTypeUtils.getClickTypeFromEvent(bukkitClickEvent.getEvent(), BetterGUI.getInstance().getMainConfig().modernClickType);
      BatchRunnable batchRunnable = new BatchRunnable();
      clickActionHandler.apply(clickEvent.getViewerID(), clickType, batchRunnable);
      BetterGUI.runBatchRunnable(batchRunnable);
    });
  }
}
