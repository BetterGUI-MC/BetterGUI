package me.hsgamer.bettergui.menu;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SlotUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.gui.GUIDisplay;
import me.hsgamer.hscore.bukkit.gui.GUIHolder;
import me.hsgamer.hscore.bukkit.gui.simple.SimpleButtonMap;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class SimpleMenu extends Menu {
  private final ActionApplier openActionApplier;
  private final ActionApplier closeActionApplier;
  private final RequirementApplier viewRequirementApplier;
  private final RequirementApplier closeRequirementApplier;
  private final GUIHolder guiHolder;
  private final SimpleButtonMap buttonMap;
  private final Set<UUID> forceClose = new ConcurrentSkipListSet<>();
  private final Map<UUID, BukkitTask> updateTasks = new ConcurrentHashMap<>();
  private long ticks = 0;
  private Permission permission = new Permission(getInstance().getName().toLowerCase() + "." + getName());

  public SimpleMenu(Config config) {
    super(config);
    guiHolder = new GUIHolder(getInstance()) {
      @Override
      public GUIDisplay createDisplay(UUID uuid) {
        GUIDisplay guiDisplay = super.createDisplay(uuid);
        if (ticks >= 0) {
          updateTasks.put(uuid, Bukkit.getScheduler().runTaskTimerAsynchronously(getInstance(), guiDisplay::update, ticks, ticks));
        }
        return guiDisplay;
      }

      @Override
      public void removeDisplay(UUID uuid) {
        super.removeDisplay(uuid);
        Optional.ofNullable(updateTasks.remove(uuid)).ifPresent(BukkitTask::cancel);
      }

      @Override
      protected void onOpen(InventoryOpenEvent event) {
        BetterGUI.runBatchRunnable(batchRunnable ->
          batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE)
            .addLast(process ->
              openActionApplier.accept(event.getPlayer().getUniqueId(), process)
            )
        );
      }

      @Override
      protected void onClose(InventoryCloseEvent event) {
        BetterGUI.runBatchRunnable(batchRunnable ->
          batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE)
            .addLast(process ->
              closeActionApplier.accept(event.getPlayer().getUniqueId(), process)
            )
        );
      }
    };
    buttonMap = new SimpleButtonMap();
    guiHolder.setButtonMap(buttonMap);

    ActionApplier tempOpenActionApplier = new ActionApplier(Collections.emptyList());
    ActionApplier tempCloseActionApplier = new ActionApplier(Collections.emptyList());
    RequirementApplier tempViewRequirementApplier = new RequirementApplier(this, getName(), Collections.emptyMap());
    RequirementApplier tempCloseRequirementApplier = new RequirementApplier(this, getName(), Collections.emptyMap());
    for (Map.Entry<String, Object> entry : config.getNormalizedValues(false).entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (!(value instanceof Map)) {
        continue;
      }
      //noinspection unchecked
      Map<String, Object> values = new CaseInsensitiveStringMap<>((Map<String, Object>) value);

      if (key.equalsIgnoreCase("menu-settings")) {

        tempOpenActionApplier = Optional.ofNullable(values.get("open-action")).map(o -> new ActionApplier(this, o)).orElse(tempOpenActionApplier);

        tempCloseActionApplier = Optional.ofNullable(values.get("close-action")).map(o -> new ActionApplier(this, o)).orElse(tempCloseActionApplier);

        Optional.ofNullable(values.get("inventory-type")).ifPresent(o -> {
          try {
            this.guiHolder.setInventoryType(InventoryType.valueOf(String.valueOf(o).toUpperCase(Locale.ROOT)));
          } catch (IllegalArgumentException e) {
            getInstance().getLogger().warning(() -> "The menu \"" + getName() + "\" contains an illegal inventory type");
          }
        });
        Optional.ofNullable(values.get("inventory")).ifPresent(o -> {
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
          .map(i -> i * 9)
          .ifPresent(this.guiHolder::setSize);
        Optional.ofNullable(values.get("slots")).map(String::valueOf).ifPresent(s -> guiHolder.setSizeFunction(uuid -> {
          String slots = StringReplacerApplier.replace(s, uuid, this);
          return Validate.getNumber(slots).map(BigDecimal::intValue).map(i -> Math.max(1, i)).orElse(9);
        }));

        this.ticks = Optional.ofNullable(MapUtil.getIfFound(values, "auto-refresh", "ticks")).map(String::valueOf).flatMap(Validate::getNumber).map(BigDecimal::longValue).orElse(this.ticks);

        tempViewRequirementApplier = Optional.ofNullable(values.get("view-requirement"))
          .filter(Map.class::isInstance)
          .<Map<String, Object>>map(Map.class::cast)
          .map(m -> new RequirementApplier(this, getName() + "_view", m))
          .orElse(tempViewRequirementApplier);

        tempCloseRequirementApplier = Optional.ofNullable(values.get("close-requirement"))
          .filter(Map.class::isInstance)
          .<Map<String, Object>>map(Map.class::cast)
          .map(m -> new RequirementApplier(this, getName() + "_close", m))
          .orElse(tempCloseRequirementApplier);

        this.permission = Optional.ofNullable(values.get("permission")).map(String::valueOf).map(Permission::new).orElse(this.permission);

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

        Optional.ofNullable(MapUtil.getIfFound(values, "name", "title")).map(String::valueOf).ifPresent(s -> guiHolder.setTitleFunction(uuid -> StringReplacerApplier.replace(s, uuid, this)));
      } else if (key.equalsIgnoreCase("default-icon") || key.equalsIgnoreCase("default-button")) {
        ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(this, "menu_" + getName() + "_button_" + key, values)).ifPresent(button -> {
          button.init();
          buttonMap.setDefaultButton(button);
        });
      } else {
        ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(this, "menu_" + getName() + "_button_" + key, values)).ifPresent(button -> {
          button.init();
          SlotUtil.getSlots(values).forEach(slot -> buttonMap.setButton(slot, button));
        });
      }
    }

    this.openActionApplier = tempOpenActionApplier;
    this.closeActionApplier = tempCloseActionApplier;
    this.viewRequirementApplier = tempViewRequirementApplier;
    this.closeRequirementApplier = tempCloseRequirementApplier;

    guiHolder.init();
    guiHolder.setCloseFilter(uuid -> {
      if (forceClose.contains(uuid)) {
        forceClose.remove(uuid);
        return true;
      }
      Requirement.Result result = closeRequirementApplier.getResult(uuid);
      BetterGUI.runBatchRunnable(batchRunnable -> batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
        result.applier.accept(uuid, process);
        process.next();
      }));
      return true;
    });
  }

  @Override
  public boolean create(Player player, String[] args, boolean bypass) {
    UUID uuid = player.getUniqueId();

    buttonMap.getButtonSlotMap().values()
      .stream()
      .filter(WrappedButton.class::isInstance)
      .map(WrappedButton.class::cast)
      .forEach(button -> button.refresh(uuid));

    if (!bypass && !player.hasPermission(permission)) {
      MessageUtils.sendMessage(player, getInstance().getMessageConfig().noPermission);
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

    // Create Inventory
    guiHolder.createDisplay(uuid).setForceUpdate(getInstance().getMainConfig().forcedUpdateInventory).init();
    return true;
  }

  @Override
  public void update(Player player) {
    guiHolder.getDisplay(player.getUniqueId()).ifPresent(GUIDisplay::update);
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

  @Override
  public GUIHolder getOriginal() {
    return guiHolder;
  }
}
