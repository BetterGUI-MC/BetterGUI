package me.hsgamer.bettergui.menu;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.argument.ArgumentHandler;
import me.hsgamer.bettergui.builder.ArgumentProcessorBuilder;
import me.hsgamer.bettergui.builder.InventoryBuilder;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.PathStringUtil;
import me.hsgamer.bettergui.util.PlayerUtil;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIDisplay;
import me.hsgamer.hscore.bukkit.gui.BukkitGUIHolder;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.bukkit.scheduler.Task;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
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
import org.bukkit.entity.HumanEntity;
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

public abstract class BaseInventoryMenu<B extends ButtonMap> extends Menu {
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
    argumentHandler = new ArgumentHandler(this);
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
        argumentHandler.onClear(display.getUniqueId());

        Optional.ofNullable(updateTasks.remove(display.getUniqueId())).ifPresent(Task::cancel);
        for (HumanEntity humanEntity : display.getInventory().getViewers()) {
          UUID uuid = humanEntity.getUniqueId();
          if (uuid != display.getUniqueId()) {
            forceClose.add(uuid);
          }
        }
        super.onRemoveDisplay(display);
      }
    };

    List<Consumer<BukkitGUIHolder>> postInitActions = new ArrayList<>();
    long tempTicks = 0;
    List<Permission> tempPermissions = Collections.singletonList(new Permission(getInstance().getName().toLowerCase() + "." + getName()));
    RequirementApplier tempViewRequirementApplier = new RequirementApplier(this, getName(), Collections.emptyMap());
    for (Map.Entry<String, Object> entry : PathStringUtil.asStringMap(config.getNormalizedValues(false)).entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (!(value instanceof Map)) {
        continue;
      }
      //noinspection unchecked
      Map<String, Object> values = new CaseInsensitiveStringMap<>((Map<String, Object>) value);

      if (key.equalsIgnoreCase("menu-settings")) {

        Optional.ofNullable(values.get("open-action"))
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

        Optional.ofNullable(values.get("close-action"))
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

        Optional.ofNullable(MapUtils.getIfFound(values, "inventory-type", "inventory")).ifPresent(o -> {
          try {
            this.guiHolder.setInventoryType(InventoryType.valueOf(String.valueOf(o).toUpperCase(Locale.ROOT)));
          } catch (IllegalArgumentException e) {
            getInstance().getLogger().warning(() -> "The menu \"" + getName() + "\" contains an illegal inventory type");
          }
        });

        Optional.ofNullable(values.get("rows"))
          .map(String::valueOf)
          .flatMap(Validate::getNumber)
          .map(BigDecimal::intValue)
          .map(i -> Math.max(1, i)).map(i -> i * 9)
          .ifPresent(guiHolder::setSize);
        Optional.ofNullable(values.get("slots"))
          .map(String::valueOf)
          .flatMap(Validate::getNumber)
          .map(BigDecimal::intValue)
          .map(i -> Math.max(1, i))
          .ifPresent(guiHolder::setSize);

        tempTicks = Optional.ofNullable(MapUtils.getIfFound(values, "auto-refresh", "ticks"))
          .map(String::valueOf)
          .flatMap(Validate::getNumber)
          .map(BigDecimal::longValue)
          .orElse(tempTicks);

        tempViewRequirementApplier = Optional.ofNullable(values.get("view-requirement"))
          .flatMap(MapUtils::castOptionalStringObjectMap)
          .map(m -> new RequirementApplier(this, getName() + "_view", m))
          .orElse(tempViewRequirementApplier);

        Optional.ofNullable(values.get("cached"))
          .map(String::valueOf)
          .map(Boolean::parseBoolean)
          .ifPresent(cached -> {
            guiHolder.addEventConsumer(CloseEvent.class, closeEvent -> closeEvent.setRemoveDisplay(!cached));
          });

        Optional.ofNullable(values.get("close-requirement"))
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

        tempPermissions = Optional.ofNullable(values.get("permission"))
          .map(o -> CollectionUtils.createStringListFromObject(o, true))
          .map(l -> l.stream().map(Permission::new).collect(Collectors.toList()))
          .orElse(tempPermissions);

        Optional.ofNullable(values.get("command"))
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

        Optional.ofNullable(MapUtils.getIfFound(values, "name", "title"))
          .map(String::valueOf)
          .ifPresent(s -> guiHolder.setTitleFunction(uuid -> StringReplacerApplier.replace(s, uuid, this)));

        Optional.ofNullable(values.get("creator"))
          .map(String::valueOf)
          .flatMap(s -> InventoryBuilder.INSTANCE.build(s, Pair.of(this, values)))
          .ifPresent(guiHolder::setInventoryFunction);

        Optional.ofNullable(MapUtils.getIfFound(values, "argument-processor", "arg-processor"))
          .map(o -> CollectionUtils.createStringListFromObject(o, true))
          .ifPresent(list -> {
            for (String s : list) {
              ArgumentProcessorBuilder.INSTANCE.build(s, this).ifPresent(argumentHandler::addProcessor);
            }
          });

        long clickDelay = Optional.ofNullable(MapUtils.getIfFound(values, "click-delay"))
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
      }
    }

    this.ticks = tempTicks;
    this.permissions = tempPermissions;
    this.viewRequirementApplier = tempViewRequirementApplier;

    buttonMap = createButtonMap(config);
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
    if (!bypass && !PlayerUtil.hasAnyPermission(player, permissions)) {
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
    argumentHandler.clearProcessors();
  }

  protected abstract B createButtonMap(Config config);

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
