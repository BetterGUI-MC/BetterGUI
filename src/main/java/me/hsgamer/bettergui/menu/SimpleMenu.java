package me.hsgamer.bettergui.menu;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.requirement.RequirementSetting;
import me.hsgamer.bettergui.utils.CommonStringReplacers;
import me.hsgamer.bettergui.utils.SlotUtils;
import me.hsgamer.hscore.bukkit.gui.simple.SimpleGUIDisplay;
import me.hsgamer.hscore.bukkit.gui.simple.SimpleGUIHolder;
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
  private final SimpleGUIHolder guiHolder;
  private final List<Action> openActions = new LinkedList<>();
  private final List<Action> closeActions = new LinkedList<>();
  private final RequirementSetting viewRequirement = new RequirementSetting(this, getName() + "_view");
  private final RequirementSetting closeRequirement = new RequirementSetting(this, getName() + "_close");
  private final Set<UUID> forceClose = new ConcurrentSkipListSet<>();
  private final Map<UUID, BukkitTask> updateTasks = new ConcurrentHashMap<>();
  private long ticks = 0;
  private Permission permission = new Permission(getInstance().getName().toLowerCase() + "." + getName());

  /**
   * Create a new menu
   *
   * @param name the name of the menu
   */
  public SimpleMenu(String name) {
    super(name);
    guiHolder = new SimpleGUIHolder(getInstance(), true) {
      @Override
      public SimpleGUIDisplay createDisplay(UUID uuid) {
        SimpleGUIDisplay guiDisplay = super.createDisplay(uuid);
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
        TaskChain<?> taskChain = BetterGUI.newChain();
        UUID uuid = event.getPlayer().getUniqueId();
        openActions.forEach(action -> action.addToTaskChain(uuid, taskChain));
        taskChain.execute();
      }

      @Override
      protected void onClose(InventoryCloseEvent event) {
        TaskChain<?> taskChain = BetterGUI.newChain();
        UUID uuid = event.getPlayer().getUniqueId();
        closeActions.forEach(action -> action.addToTaskChain(uuid, taskChain));
        taskChain.execute();
      }
    };

    guiHolder.init();
    guiHolder.setCloseFilter(uuid -> {
      if (forceClose.contains(uuid)) {
        forceClose.remove(uuid);
        return true;
      }
      if (!closeRequirement.check(uuid)) {
        closeRequirement.sendFailActions(uuid);
        return false;
      }
      closeRequirement.getCheckedRequirement(uuid).ifPresent(requirementSet -> {
        requirementSet.take(uuid);
        requirementSet.sendSuccessActions(uuid);
      });
      return true;
    });
  }

  @Override
  public void setFromConfig(Config config) {
    config.getNormalizedValues(false).forEach((key, value) -> {
      if (!(value instanceof Map)) {
        return;
      }
      Map<String, Object> values = new CaseInsensitiveStringMap<>((Map<String, Object>) value);

      if (key.equalsIgnoreCase("menu-settings")) {

        Optional.ofNullable(values.get("open-action")).ifPresent(o -> this.openActions.addAll(ActionBuilder.INSTANCE.getActions(this, o)));

        Optional.ofNullable(values.get("close-action")).ifPresent(o -> this.closeActions.addAll(ActionBuilder.INSTANCE.getActions(this, o)));

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

        this.ticks = Optional.ofNullable(values.get("auto-refresh")).map(String::valueOf).flatMap(Validate::getNumber).map(BigDecimal::longValue).orElse(this.ticks);
        this.ticks = Optional.ofNullable(values.get("ticks")).map(String::valueOf).flatMap(Validate::getNumber).map(BigDecimal::longValue).orElse(this.ticks);

        Optional.ofNullable(values.get("view-requirement"))
          .filter(Map.class::isInstance)
          .map(o -> (Map<String, Object>) o)
          .ifPresent(this.viewRequirement::loadFromSection);

        Optional.ofNullable(values.get("close-requirement"))
          .filter(Map.class::isInstance)
          .map(o -> (Map<String, Object>) o)
          .ifPresent(this.closeRequirement::loadFromSection);

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

        Optional.ofNullable(values.get("name")).map(String::valueOf).ifPresent(s -> guiHolder.setTitleFunction(uuid -> {
          String title = s;
          title = CommonStringReplacers.VARIABLE.replace(title, uuid);
          title = CommonStringReplacers.EXPRESSION.replace(title, uuid);
          title = CommonStringReplacers.COLORIZE.replace(title, uuid);
          return title;
        }));
        Optional.ofNullable(values.get("title")).map(String::valueOf).ifPresent(s -> guiHolder.setTitleFunction(uuid -> {
          String title = s;
          title = CommonStringReplacers.VARIABLE.replace(title, uuid);
          title = CommonStringReplacers.EXPRESSION.replace(title, uuid);
          title = CommonStringReplacers.COLORIZE.replace(title, uuid);
          return title;
        }));
      } else if (key.equalsIgnoreCase("default-icon") || key.equalsIgnoreCase("default-button")) {
        WrappedButton button = ButtonBuilder.INSTANCE.getButton(this, "menu_" + getName() + "_button_" + key, values);
        button.init();
        guiHolder.setDefaultButton(button);
      } else {
        WrappedButton button = ButtonBuilder.INSTANCE.getButton(this, "menu_" + getName() + "_button_" + key, values);
        button.init();
        SlotUtils.getSlots(values).forEach(slot -> guiHolder.setButton(slot, button));
      }
    });
  }

  @Override
  public boolean createInventory(Player player, String[] args, boolean bypass) {
    guiHolder.getButtonSlotMap().values()
      .stream()
      .filter(WrappedButton.class::isInstance)
      .forEach(button -> ((WrappedButton) button).refresh(player.getUniqueId()));

    UUID uuid = player.getUniqueId();

    if (!bypass && !player.hasPermission(permission)) {
      MessageUtils.sendMessage(player, MessageConfig.NO_PERMISSION.getValue());
      return false;
    }

    // Check Requirement
    if (!bypass) {
      if (!viewRequirement.check(uuid)) {
        viewRequirement.sendFailActions(uuid);
        return false;
      }
      viewRequirement.getCheckedRequirement(uuid).ifPresent(requirementSet -> {
        requirementSet.take(uuid);
        requirementSet.sendSuccessActions(uuid);
      });
    }

    // Create Inventory
    guiHolder.createDisplay(uuid).setForceUpdate(Boolean.TRUE.equals(MainConfig.FORCED_UPDATE_INVENTORY.getValue())).init();
    return true;
  }

  @Override
  public void updateInventory(Player player) {
    guiHolder.getDisplay(player.getUniqueId()).ifPresent(SimpleGUIDisplay::update);
  }

  @Override
  public void closeInventory(Player player) {
    forceClose.add(player.getUniqueId());
    player.closeInventory();
  }

  @Override
  public void closeAll() {
    guiHolder.stop();
  }

  @Override
  public Object getOriginal() {
    return guiHolder;
  }
}
