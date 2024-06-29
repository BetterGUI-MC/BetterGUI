package me.hsgamer.bettergui.menu;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.common.task.Task;
import io.github.projectunified.minelib.scheduler.entity.EntityScheduler;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.builder.InventoryBuilder;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIDisplay;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIHolder;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.minecraft.gui.button.ButtonMap;
import me.hsgamer.hscore.minecraft.gui.event.ClickEvent;
import me.hsgamer.hscore.minecraft.gui.event.CloseEvent;
import me.hsgamer.hscore.minecraft.gui.event.OpenEvent;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

/**
 * A {@link BaseMenu} for menus using {@link BukkitGUIHolder}
 *
 * @param <B> the type of the {@link ButtonMap} to use in the {@link BukkitGUIHolder}
 */
public abstract class BaseInventoryMenu<B extends ButtonMap> extends BaseMenu {
  private final BukkitGUIHolder guiHolder;
  private final B buttonMap;
  private final Set<UUID> forceClose = new ConcurrentSkipListSet<>();
  private final Map<UUID, Task> updateTasks = new ConcurrentHashMap<>();
  private final long ticks;

  protected BaseInventoryMenu(Config config) {
    super(config);
    guiHolder = new BukkitGUIHolder(getInstance()) {
      @Override
      protected @NotNull BukkitGUIDisplay newDisplay(UUID uuid) {
        BukkitGUIDisplay guiDisplay = super.newDisplay(uuid);
        if (ticks >= 0) {
          Player player = Bukkit.getPlayer(uuid);
          assert player != null;

          updateTasks.put(uuid, AsyncScheduler.get(BetterGUI.getInstance()).runTimer(() -> {
            if (player.isOnline()) {
              guiDisplay.update();
              return true;
            } else {
              return false;
            }
          }, ticks, ticks));
        }
        return guiDisplay;
      }

      @Override
      protected void onRemoveDisplay(@NotNull BukkitGUIDisplay display) {
        Optional.ofNullable(updateTasks.remove(display.getUniqueId())).ifPresent(Task::cancel);
        super.onRemoveDisplay(display);
      }

      @Override
      protected void onOpen(@NotNull OpenEvent event) {
        if (!openActionApplier.isEmpty()) {
          UUID uuid = event.getViewerID();
          BatchRunnable batchRunnable = new BatchRunnable();
          batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> openActionApplier.accept(uuid, process));
          AsyncScheduler.get(BetterGUI.getInstance()).run(batchRunnable);
        }
      }

      @Override
      protected void onClose(@NotNull CloseEvent event) {
        UUID uuid = event.getViewerID();

        if (!closeActionApplier.isEmpty()) {
          BatchRunnable batchRunnable = new BatchRunnable();
          batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> closeActionApplier.accept(uuid, process));
          AsyncScheduler.get(BetterGUI.getInstance()).run(batchRunnable);
        }

        if (!closeRequirementApplier.isEmpty()) {
          if (forceClose.contains(uuid)) {
            forceClose.remove(uuid);
            return;
          }
          Requirement.Result result = closeRequirementApplier.getResult(uuid);

          BatchRunnable batchRunnable = new BatchRunnable();
          batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
            result.applier.accept(uuid, process);
            process.next();
          });
          AsyncScheduler.get(BetterGUI.getInstance()).run(batchRunnable);

          if (!result.isSuccess) {
            event.setRemoveDisplay(false);
            guiHolder.getDisplay(uuid).ifPresent(display -> {
              Player player = Bukkit.getPlayer(uuid);
              if (player != null) {
                EntityScheduler.get(BetterGUI.getInstance(), player).run(() -> player.openInventory(display.getInventory()));
              }
            });
          }
        }
      }
    };

    Optional.ofNullable(MapUtils.getIfFound(menuSettings, "inventory-type", "inventory")).ifPresent(o -> {
      try {
        this.guiHolder.setInventoryType(InventoryType.valueOf(String.valueOf(o).toUpperCase(Locale.ROOT)));
      } catch (IllegalArgumentException e) {
        getInstance().getLogger().warning(() -> "The menu \"" + getName() + "\" contains an illegal inventory type");
      }
    });

    Optional.ofNullable(menuSettings.get("rows"))
      .map(String::valueOf)
      .flatMap(Validate::getNumber)
      .map(BigDecimal::intValue)
      .map(i -> Math.max(1, i)).map(i -> i * 9)
      .ifPresent(guiHolder::setSize);
    Optional.ofNullable(menuSettings.get("slots"))
      .map(String::valueOf)
      .flatMap(Validate::getNumber)
      .map(BigDecimal::intValue)
      .map(i -> Math.max(1, i))
      .ifPresent(guiHolder::setSize);

    ticks = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "auto-refresh", "ticks"))
      .map(String::valueOf)
      .flatMap(Validate::getNumber)
      .map(BigDecimal::longValue)
      .orElse(0L);

    Optional.ofNullable(menuSettings.get("cached"))
      .map(String::valueOf)
      .map(Boolean::parseBoolean)
      .ifPresent(cached -> {
        guiHolder.addEventConsumer(CloseEvent.class, closeEvent -> closeEvent.setRemoveDisplay(!cached));
      });

    Optional.ofNullable(MapUtils.getIfFound(menuSettings, "name", "title"))
      .map(String::valueOf)
      .ifPresent(s -> guiHolder.setTitleFunction(uuid -> StringReplacerApplier.replace(s, uuid, this)));

    Optional.ofNullable(menuSettings.get("creator"))
      .map(String::valueOf)
      .flatMap(s -> InventoryBuilder.INSTANCE.build(s, Pair.of(this, menuSettings)))
      .ifPresent(guiHolder::setInventoryFunction);

    long clickDelay = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "click-delay"))
      .map(String::valueOf)
      .flatMap(Validate::getNumber)
      .map(BigDecimal::longValue)
      .orElse(50L);
    if (clickDelay > 0) {
      guiHolder.addEventConsumer(ClickEvent.class, new Consumer<ClickEvent>() {
        private final Map<UUID, Long> lastClickMap = new ConcurrentHashMap<>();

        @Override
        public void accept(ClickEvent clickEvent) {
          long currentTime = System.currentTimeMillis();
          long lastClick = lastClickMap.getOrDefault(clickEvent.getViewerID(), 0L);
          if (currentTime - lastClick < clickDelay) {
            clickEvent.setButtonExecute(false);
            return;
          }
          lastClickMap.put(clickEvent.getViewerID(), currentTime);
        }
      });
    }

    buttonMap = createButtonMap();
    guiHolder.setButtonMap(buttonMap);

    guiHolder.init();
  }

  @Override
  protected boolean createChecked(Player player, String[] args, boolean bypass) {
    UUID uuid = player.getUniqueId();
    refreshButtonMapOnCreate(buttonMap, uuid);
    guiHolder.createDisplay(uuid).open();
    return true;
  }

  @Override
  public void update(Player player) {
    guiHolder.getDisplay(player.getUniqueId()).ifPresent(BukkitGUIDisplay::update);
  }

  @Override
  public void close(Player player) {
    forceClose.add(player.getUniqueId());
    player.closeInventory();
  }

  @Override
  public void closeAll() {
    guiHolder.stop();
  }

  protected abstract B createButtonMap();

  protected void refreshButtonMapOnCreate(B buttonMap, UUID uuid) {
    // EMPTY
  }

  public B getButtonMap() {
    return buttonMap;
  }

  public BukkitGUIHolder getGUIHolder() {
    return guiHolder;
  }
}
