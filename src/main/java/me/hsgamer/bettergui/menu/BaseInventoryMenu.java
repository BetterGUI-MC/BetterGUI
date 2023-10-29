package me.hsgamer.bettergui.menu;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.menu.StandardMenu;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.argument.ArgumentHandler;
import me.hsgamer.bettergui.builder.InventoryBuilder;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIDisplay;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIHolder;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.bukkit.scheduler.Task;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.bukkit.utils.PermissionUtils;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.minecraft.gui.button.ButtonMap;
import me.hsgamer.hscore.minecraft.gui.event.ClickEvent;
import me.hsgamer.hscore.minecraft.gui.event.CloseEvent;
import me.hsgamer.hscore.minecraft.gui.event.OpenEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public abstract class BaseInventoryMenu<B extends ButtonMap> extends StandardMenu {
  private final RequirementApplier viewRequirementApplier;
  private final BukkitGUIHolder guiHolder;
  private final B buttonMap;
  private final Set<UUID> forceClose = new ConcurrentSkipListSet<>();
  private final Map<UUID, Task> updateTasks = new ConcurrentHashMap<>();
  private final long ticks;
  private final List<Permission> permissions;
  private final ArgumentHandler argumentHandler;

  protected BaseInventoryMenu(Config config) {
    super(config);
    guiHolder = new BukkitGUIHolder(getInstance()) {
      @Override
      protected @NotNull BukkitGUIDisplay newDisplay(UUID uuid) {
        BukkitGUIDisplay guiDisplay = super.newDisplay(uuid);
        if (ticks >= 0) {
          Player player = Bukkit.getPlayer(uuid);
          updateTasks.put(uuid, Scheduler.current().async().runEntityTaskTimer(player, guiDisplay::update, ticks, ticks));
        }
        return guiDisplay;
      }

      @Override
      protected void onRemoveDisplay(@NotNull BukkitGUIDisplay display) {
        Optional.ofNullable(updateTasks.remove(display.getUniqueId())).ifPresent(Task::cancel);
        super.onRemoveDisplay(display);
      }
    };

    List<Consumer<BukkitGUIHolder>> postInitActions = new ArrayList<>();
    Optional.ofNullable(menuSettings.get("open-action"))
      .map(o -> new ActionApplier(this, o))
      .ifPresent(actionApplier -> postInitActions.add(holder -> holder.addEventConsumer(OpenEvent.class, openEvent -> {
        UUID uuid = openEvent.getViewerID();
        BetterGUI.runBatchRunnable(batchRunnable ->
          batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE)
            .addLast(process ->
              actionApplier.accept(uuid, process)
            )
        );
      })));

    Optional.ofNullable(menuSettings.get("close-action"))
      .map(o -> new ActionApplier(this, o))
      .ifPresent(actionApplier -> postInitActions.add(holder -> holder.addEventConsumer(CloseEvent.class, openEvent -> {
        UUID uuid = openEvent.getViewerID();
        BetterGUI.runBatchRunnable(batchRunnable ->
          batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE)
            .addLast(process ->
              actionApplier.accept(uuid, process)
            )
        );
      })));

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

    viewRequirementApplier = Optional.ofNullable(menuSettings.get("view-requirement"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .map(m -> new RequirementApplier(this, getName() + "_view", m))
      .orElseGet(() -> new RequirementApplier(this, getName(), Collections.emptyMap()));

    Optional.ofNullable(menuSettings.get("cached"))
      .map(String::valueOf)
      .map(Boolean::parseBoolean)
      .ifPresent(cached -> {
        guiHolder.addEventConsumer(CloseEvent.class, closeEvent -> closeEvent.setRemoveDisplay(!cached));
      });

    Optional.ofNullable(menuSettings.get("close-requirement"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .map(m -> new RequirementApplier(this, getName() + "_close", m))
      .ifPresent(closeRequirementApplier -> {
        guiHolder.addEventConsumer(CloseEvent.class, closeEvent -> {
          UUID uuid = closeEvent.getViewerID();
          if (forceClose.contains(uuid)) {
            forceClose.remove(uuid);
            return;
          }
          Requirement.Result result = closeRequirementApplier.getResult(uuid);
          BetterGUI.runBatchRunnable(batchRunnable -> batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
            result.applier.accept(uuid, process);
            process.next();
          }));

          if (!result.isSuccess) {
            closeEvent.setRemoveDisplay(false);
            guiHolder.getDisplay(uuid).ifPresent(display -> {
              Player player = Bukkit.getPlayer(uuid);
              if (player != null) {
                Scheduler.current().sync().runEntityTask(player, () -> player.openInventory(display.getInventory()));
              }
            });
          }
        });
      });

    permissions = Optional.ofNullable(menuSettings.get("permission"))
      .map(o -> CollectionUtils.createStringListFromObject(o, true))
      .map(l -> l.stream().map(Permission::new).collect(Collectors.toList()))
      .orElseGet(() -> Collections.singletonList(new Permission(getInstance().getName().toLowerCase() + "." + getName())));

    Optional.ofNullable(menuSettings.get("command"))
      .map(o -> CollectionUtils.createStringListFromObject(o, true))
      .ifPresent(list -> {
        for (String s : list) {
          if (s.contains(" ")) {
            getInstance().getLogger().warning("Illegal characters in command '" + s + "'" + "in the menu '" + getName() + "'. Ignored");
          } else {
            getInstance().getMenuCommandManager().registerMenuCommand(s, this);
          }
        }
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

    argumentHandler = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "argument-processor", "arg-processor", "argument", "arg"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .map(m -> new ArgumentHandler(this, m))
      .orElseGet(() -> new ArgumentHandler(this, Collections.emptyMap()));

    buttonMap = createButtonMap();
    guiHolder.setButtonMap(buttonMap);

    guiHolder.init();
    postInitActions.forEach(action -> action.accept(guiHolder));
  }

  @Override
  public boolean create(Player player, String[] args, boolean bypass) {
    UUID uuid = player.getUniqueId();

    // Check Argument
    if (!argumentHandler.process(uuid, args).isPresent()) {
      return false;
    }

    // Refresh Button Map
    refreshButtonMapOnCreate(buttonMap, uuid);

    // Check Permission
    if (!bypass && !PermissionUtils.hasAnyPermission(player, permissions)) {
      MessageUtils.sendMessage(player, getInstance().getMessageConfig().getNoPermission());
      return false;
    }

    // Check Requirement
    if (!bypass) {
      Requirement.Result result = viewRequirementApplier.getResult(uuid);
      BetterGUI.runBatchRunnable(batchRunnable -> batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
        result.applier.accept(uuid, process);
        process.next();
      }));
      if (!result.isSuccess) {
        return false;
      }
    }

    // Open Inventory
    guiHolder.createDisplay(uuid).open();
    return true;
  }

  @Override
  public List<String> tabComplete(Player player, String[] args) {
    return argumentHandler.handleTabComplete(player.getUniqueId(), args);
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

  public ArgumentHandler getArgumentHandler() {
    return argumentHandler;
  }
}
