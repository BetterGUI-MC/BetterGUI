package me.hsgamer.bettergui.object.menu;

import static me.hsgamer.bettergui.BetterGUI.getInstance;
import static me.hsgamer.bettergui.BetterGUI.newChain;

import co.aikar.taskchain.TaskChain;
import fr.mrmicky.fastinv.FastInv;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.CommandBuilder;
import me.hsgamer.bettergui.builder.IconBuilder;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.manager.VariableManager;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.GlobalRequirement;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.ParentIcon;
import me.hsgamer.bettergui.object.menu.SimpleMenu.SimpleInventory;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SimpleMenu extends Menu<SimpleInventory> {

  private final Map<UUID, SimpleInventory> inventoryMap = new HashMap<>();

  private final Map<Integer, Icon> icons = new HashMap<>();
  private final List<Command> openActions = new ArrayList<>();
  private final List<Command> closeActions = new ArrayList<>();
  private InventoryType inventoryType = InventoryType.CHEST;
  private String title;
  private boolean titleHasVariable = false;
  private int maxSlots = 27;
  private long ticks = 0;
  private Permission permission = new Permission(
      getInstance().getName().toLowerCase() + "." + getName());
  private Icon defaultIcon;
  private GlobalRequirement globalRequirement;

  public SimpleMenu(String name) {
    super(name);
  }

  @Override
  public void setFromFile(FileConfiguration file) {
    for (String key : file.getKeys(false)) {
      if (key.equalsIgnoreCase("menu-settings")) {
        Map<String, Object> keys = new CaseInsensitiveStringMap<>(
            file.getConfigurationSection(key).getValues(false));

        if (keys.containsKey(Settings.NAME)) {
          title = String.valueOf(keys.get(Settings.NAME));
          titleHasVariable = VariableManager.hasVariables(title);
        }

        if (keys.containsKey(Settings.INVENTORY_TYPE)) {
          try {
            inventoryType = InventoryType
                .valueOf((String.valueOf(keys.get(Settings.INVENTORY_TYPE))).toUpperCase());
          } catch (IllegalArgumentException e) {
            getInstance().getLogger().log(Level.WARNING, "The menu \"" + file.getName()
                + "\" contains an illegal inventory type, it will be CHEST by default");
          }
          switch (inventoryType) {
            case FURNACE:
            case ENDER_CHEST:
            case CHEST:
            case HOPPER:
            case WORKBENCH:
            case DISPENSER:
            case DROPPER:
              maxSlots = inventoryType.getDefaultSize();
              break;
            default:
              inventoryType = InventoryType.CHEST;
              getInstance().getLogger().log(Level.WARNING, "The menu \"" + file.getName()
                  + "\"'s inventory type is not supported, it will be CHEST by default");
          }
        } else if (keys.containsKey(Settings.ROWS)) {
          int temp = Integer.parseInt(String.valueOf(keys.get(Settings.ROWS))) * 9;
          maxSlots = temp > 0 ? temp : maxSlots;
        }

        if (keys.containsKey(Settings.COMMAND)) {
          CommonUtils.createStringListFromObject(keys.get(Settings.COMMAND), true)
              .forEach(s -> getInstance().getCommandManager().registerMenuCommand(s, this));
        }

        if (keys.containsKey(Settings.OPEN_ACTION)) {
          openActions.addAll(
              CommandBuilder.getCommands(null,
                  CommonUtils.createStringListFromObject(keys.get(Settings.OPEN_ACTION), true)));
        }
        if (keys.containsKey(Settings.CLOSE_ACTION)) {
          closeActions.addAll(
              CommandBuilder.getCommands(null,
                  CommonUtils.createStringListFromObject(keys.get(Settings.CLOSE_ACTION), true)));
        }

        if (keys.containsKey(Settings.PERMISSION)) {
          permission = new Permission(String.valueOf(keys.get(Settings.PERMISSION)));
        }

        if (keys.containsKey(Settings.AUTO_REFRESH)) {
          ticks = Integer.parseInt(String.valueOf(keys.get(Settings.AUTO_REFRESH)));
        }

        if (keys.containsKey(Settings.VIEW_REQUIREMENT)) {
          globalRequirement = new GlobalRequirement(
              (ConfigurationSection) keys.get(Settings.VIEW_REQUIREMENT));
        }
      } else if (key.equalsIgnoreCase("default-icon")) {
        defaultIcon = IconBuilder.getIcon(this, file.getConfigurationSection(key));
      } else {
        ConfigurationSection section = file.getConfigurationSection(key);
        Icon icon = IconBuilder.getIcon(this, section);
        List<Integer> slots = IconBuilder.getSlots(section);
        for (Integer slot : slots) {
          if (icons.containsKey(slot)) {
            Icon tempIcon = icons.get(slot);
            if (tempIcon instanceof ParentIcon) {
              ((ParentIcon) tempIcon).addChild(icon.cloneIcon());
            } else {
              getInstance().getLogger().warning(
                  icon.getName() + " & " + tempIcon.getName() + " from " + getName()
                      + " have the same slot. Only one of them will be set");
            }
          } else {
            if (slot < maxSlots) {
              icons.put(slot, icon.cloneIcon());
            } else {
              getInstance().getLogger().warning(
                  icon.getName() + " from " + getName() + " has invalid slot (Exceed the limit)");
            }
          }
        }
      }
    }
  }

  @Override
  public void createInventory(Player player, boolean bypass) {
    if (bypass || player.hasPermission(permission)) {
      // Check Requirement
      if (!bypass && globalRequirement != null) {
        if (!globalRequirement.check(player)) {
          globalRequirement.sendFailCommand(player);
          return;
        }
        globalRequirement.getCheckedRequirement(player).ifPresent(iconRequirementSet -> {
          iconRequirementSet.take(player);
          iconRequirementSet.sendCommand(player);
        });
      }

      // Create Inventory
      String parsedTitle = CommonUtils
          .colorize(titleHasVariable ? VariableManager.setVariables(title, player) : title);
      SimpleInventory inventory = initInventory(player, parsedTitle);

      // Add Actions
      if (!openActions.isEmpty()) {
        inventory.addOpenHandler(event -> {
          TaskChain<?> taskChain = newChain();
          openActions.forEach(action -> action.addToTaskChain(player, taskChain));
          taskChain.execute();
        });
      }
      if (!closeActions.isEmpty()) {
        inventory.addCloseHandler(event -> {
          TaskChain<?> taskChain = newChain();
          closeActions.forEach(action -> action.addToTaskChain(player, taskChain));
          taskChain.execute();
        });
      }
      inventory.open();
      inventoryMap.put(player.getUniqueId(), inventory);
    } else {
      CommonUtils
          .sendMessage(player, getInstance().getMessageConfig().get(DefaultMessage.NO_PERMISSION));
    }
  }

  private SimpleInventory initInventory(Player player, String title) {
    SimpleInventory inventory;
    if (inventoryType.equals(InventoryType.CHEST)) {
      if (title != null) {
        inventory = new SimpleInventory(player, maxSlots, title);
      } else {
        inventory = new SimpleInventory(player, maxSlots);
      }
    } else {
      if (title != null) {
        inventory = new SimpleInventory(player, inventoryType, title);
      } else {
        inventory = new SimpleInventory(player, inventoryType);
      }
    }
    return inventory;
  }

  @Override
  public void updateInventory(Player player) {
    getInventory(player).ifPresent(SimpleInventory::updateInventory);
  }

  @Override
  public void closeInventory(Player player) {
    player.closeInventory();
    inventoryMap.remove(player.getUniqueId());
  }

  @Override
  public void closeAll() {
    inventoryMap.values().forEach(simpleInventory -> simpleInventory.player.closeInventory());
    inventoryMap.clear();
  }

  @Override
  public Optional<SimpleInventory> getInventory(Player player) {
    return Optional.ofNullable(inventoryMap.get(player.getUniqueId()));
  }

  private static class Settings {

    static final String NAME = "name";
    static final String ROWS = "rows";
    static final String INVENTORY_TYPE = "inventory-type";
    static final String COMMAND = "command";
    static final String OPEN_ACTION = "open-action";
    static final String CLOSE_ACTION = "close-action";
    static final String PERMISSION = "permission";
    static final String AUTO_REFRESH = "auto-refresh";
    static final String VIEW_REQUIREMENT = "view-requirement";
  }

  protected class SimpleInventory extends FastInv {

    private final Map<Integer, Icon> cloneIcons = new HashMap<>();
    private final Player player;
    private Icon cloneDefaultIcon;
    private BukkitTask task;

    public SimpleInventory(Player player, int size, String title) {
      super(size, title != null ? title : InventoryType.CHEST.getDefaultTitle());
      this.player = player;
      icons.forEach((key, value) -> this.cloneIcons.put(key, value.cloneIcon()));
      if (defaultIcon != null) {
        this.cloneDefaultIcon = defaultIcon.cloneIcon();
      }
      createItems();
    }

    public SimpleInventory(Player player, InventoryType type, String title) {
      super(type, title != null ? title : type.getDefaultTitle());
      this.player = player;
      cloneIcons.forEach((key, value) -> this.cloneIcons.put(key, value.cloneIcon()));
      if (cloneDefaultIcon != null) {
        this.cloneDefaultIcon = cloneDefaultIcon.cloneIcon();
      }
      createItems();
    }

    public SimpleInventory(Player player, int size) {
      super(size);
      this.player = player;
      cloneIcons.forEach((key, value) -> this.cloneIcons.put(key, value.cloneIcon()));
      if (cloneDefaultIcon != null) {
        this.cloneDefaultIcon = cloneDefaultIcon.cloneIcon();
      }
      createItems();
    }

    public SimpleInventory(Player player, InventoryType type) {
      super(type);
      this.player = player;
      cloneIcons.forEach((key, value) -> this.cloneIcons.put(key, value.cloneIcon()));
      if (cloneDefaultIcon != null) {
        this.cloneDefaultIcon = cloneDefaultIcon.cloneIcon();
      }
      createItems();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
      if (ticks >= 0) {
        task = new BukkitRunnable() {
          @Override
          public void run() {
            updateInventory();
          }
        }.runTaskTimerAsynchronously(BetterGUI.getInstance(), ticks, ticks);
      }
    }

    public void updateInventory() {
      updateItems();
      player.updateInventory();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
      if (task != null) {
        task.cancel();
      }
      inventoryMap.remove(player.getUniqueId());
    }

    private void createDefaultItem(int slot) {
      if (cloneDefaultIcon != null) {
        Optional<ClickableItem> rawDefaultClickableItem = cloneDefaultIcon
            .createClickableItem(player);
        if (rawDefaultClickableItem.isPresent()) {
          ClickableItem clickableItem = rawDefaultClickableItem.get();
          setItem(slot, clickableItem.getItem(), clickableItem.getClickEvent());
        }
      }
    }

    private void updateDefaultItem(int slot) {
      if (cloneDefaultIcon != null) {
        Optional<ClickableItem> rawDefaultClickableItem = cloneDefaultIcon
            .updateClickableItem(player);
        if (rawDefaultClickableItem.isPresent()) {
          ClickableItem clickableItem = rawDefaultClickableItem.get();
          setItem(slot, clickableItem.getItem(), clickableItem.getClickEvent());
        }
      }
    }

    private void createItems() {
      for (int i = 0; i < maxSlots; i++) {
        if (cloneIcons.containsKey(i)) {
          Optional<ClickableItem> rawClickableItem = cloneIcons.get(i).createClickableItem(player);
          if (rawClickableItem.isPresent()) {
            ClickableItem clickableItem = rawClickableItem.get();
            setItem(i, clickableItem.getItem(), clickableItem.getClickEvent());
          } else {
            createDefaultItem(i);
          }
        } else {
          createDefaultItem(i);
        }
      }
    }

    private void updateItems() {
      for (int i = 0; i < maxSlots; i++) {
        if (cloneIcons.containsKey(i)) {
          Optional<ClickableItem> rawClickableItem = cloneIcons.get(i).updateClickableItem(player);
          if (rawClickableItem.isPresent()) {
            ClickableItem clickableItem = rawClickableItem.get();
            setItem(i, clickableItem.getItem(), clickableItem.getClickEvent());
          } else {
            updateDefaultItem(i);
          }
        } else {
          updateDefaultItem(i);
        }
      }
    }

    public void open() {
      open(player);
    }
  }
}
