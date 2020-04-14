package me.hsgamer.bettergui.object.menu;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import fr.mrmicky.fastinv.FastInv;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.IconBuilder;
import me.hsgamer.bettergui.builder.PropertyBuilder;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.object.GlobalRequirement;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.LocalVariable;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.ParentIcon;
import me.hsgamer.bettergui.object.menu.SimpleMenu.SimpleInventory;
import me.hsgamer.bettergui.object.property.menu.MenuAction;
import me.hsgamer.bettergui.object.property.menu.MenuInventoryType;
import me.hsgamer.bettergui.object.property.menu.MenuRequirement;
import me.hsgamer.bettergui.object.property.menu.MenuRows;
import me.hsgamer.bettergui.object.property.menu.MenuTicks;
import me.hsgamer.bettergui.object.property.menu.MenuTitle;
import me.hsgamer.bettergui.object.property.menu.MenuVariable;
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

  private final Map<UUID, SimpleInventory> inventoryMap = new ConcurrentHashMap<>();

  private final Map<Integer, Icon> icons = new HashMap<>();
  private Permission permission = new Permission(
      getInstance().getName().toLowerCase() + "." + getName());
  private Icon defaultIcon;

  private GlobalRequirement viewRequirement;
  private GlobalRequirement closeRequirement;

  private MenuAction menuCloseAction;
  private MenuAction menuOpenAction;
  private MenuInventoryType menuInventoryType;
  private MenuRows menuRows;
  private MenuTicks menuTicks;
  private MenuTitle menuTitle;

  public SimpleMenu(String name) {
    super(name);
  }

  @Override
  public void setFromFile(FileConfiguration file) {
    for (String key : file.getKeys(false)) {
      if (key.equalsIgnoreCase("menu-settings")) {
        ConfigurationSection settingsSection = file.getConfigurationSection(key);

        PropertyBuilder.loadMenuPropertiesFromSection(this, settingsSection)
            .forEach((setting, menuProperty) -> {
              if (menuProperty instanceof MenuAction) {
                if (setting.equals("open-action")) {
                  this.menuOpenAction = (MenuAction) menuProperty;
                } else if (setting.equals("close-action")) {
                  this.menuCloseAction = (MenuAction) menuProperty;
                }
              } else if (menuProperty instanceof MenuInventoryType) {
                this.menuInventoryType = (MenuInventoryType) menuProperty;
              } else if (menuProperty instanceof MenuRows) {
                this.menuRows = (MenuRows) menuProperty;
              } else if (menuProperty instanceof MenuTicks) {
                this.menuTicks = (MenuTicks) menuProperty;
              } else if (menuProperty instanceof MenuTitle) {
                this.menuTitle = (MenuTitle) menuProperty;
              } else if (menuProperty instanceof MenuRequirement) {
                if (setting.equals("view-requirement")) {
                  viewRequirement = ((MenuRequirement) menuProperty).getParsed(null);
                } else if (setting.equals("close-requirement")) {
                  closeRequirement = ((MenuRequirement) menuProperty).getParsed(null);
                  closeRequirement.getRequirements().forEach(
                      requirementSet -> requirementSet.getRequirements().forEach(requirement -> {
                        if (requirement instanceof LocalVariable) {
                          registerVariable(String.join("_", "close", requirementSet.getName(),
                              ((LocalVariable) requirement).getIdentifier()),
                              (LocalVariable) requirement);
                        }
                      }));
                }
              } else if (menuProperty instanceof MenuVariable) {
                ((MenuVariable) menuProperty).getParsed(null).forEach(menuLocalVariable -> this
                    .registerVariable(menuLocalVariable.getIdentifier(), menuLocalVariable));
              }
            });

        Map<String, Object> keys = new CaseInsensitiveStringMap<>(settingsSection.getValues(false));

        if (keys.containsKey(Settings.COMMAND)) {
          CommonUtils.createStringListFromObject(keys.get(Settings.COMMAND), true)
              .forEach(s -> {
                if (s.contains(" ")) {
                  getInstance().getLogger().warning(
                      "Illegal characters in command '" + s + "'" + "in the menu '" + getName()
                          + "'. Ignored");
                } else {
                  getInstance().getCommandManager().registerMenuCommand(s, this);
                }
              });
        }

        if (keys.containsKey(Settings.PERMISSION)) {
          permission = new Permission(String.valueOf(keys.get(Settings.PERMISSION)));
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
            icons.put(slot, icon.cloneIcon());
          }
        }
      }
    }
  }

  @Override
  public void createInventory(Player player, String[] args, boolean bypass) {
    if (bypass || player.hasPermission(permission)) {
      // Check Requirement
      if (!bypass && viewRequirement != null) {
        if (!viewRequirement.check(player)) {
          viewRequirement.sendFailCommand(player);
          return;
        }
        viewRequirement.getCheckedRequirement(player).ifPresent(iconRequirementSet -> {
          iconRequirementSet.take(player);
          iconRequirementSet.sendSuccessCommands(player);
        });
      }

      // Create Inventory
      SimpleInventory inventory = initInventory(player);

      // Add Actions
      if (menuOpenAction != null) {
        inventory.addOpenHandler(event -> menuOpenAction.getParsed(player).execute());
      }
      if (menuCloseAction != null) {
        inventory.addCloseHandler(event -> menuCloseAction.getParsed(player).execute());
      }

      inventory.open();
    } else {
      CommonUtils
          .sendMessage(player, getInstance().getMessageConfig().get(DefaultMessage.NO_PERMISSION));
    }
  }

  protected SimpleInventory initInventory(Player player) {
    SimpleInventory inventory;
    InventoryType inventoryType =
        menuInventoryType != null ? menuInventoryType.getParsed(player) : InventoryType.CHEST;
    int maxSlots;
    if (menuRows != null) {
      inventoryType = InventoryType.CHEST;
      maxSlots = menuRows.getParsed(player);
    } else {
      maxSlots = inventoryType.getDefaultSize();
    }
    String parsedTitle = menuTitle != null ? menuTitle.getParsed(player) : null;
    if (inventoryType.equals(InventoryType.CHEST)) {
      if (parsedTitle != null) {
        inventory = new SimpleInventory(player, maxSlots, parsedTitle);
      } else {
        inventory = new SimpleInventory(player, maxSlots);
      }
    } else {
      if (parsedTitle != null) {
        inventory = new SimpleInventory(player, inventoryType, parsedTitle);
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
    inventoryMap.computeIfPresent(player.getUniqueId(), ((uuid, simpleInventory) -> {
      simpleInventory.forceClose();
      return null;
    }));
  }

  @Override
  public void closeAll() {
    inventoryMap.values().forEach(SimpleInventory::forceClose);
    inventoryMap.clear();
  }

  @Override
  public Optional<SimpleInventory> getInventory(Player player) {
    return Optional.ofNullable(inventoryMap.get(player.getUniqueId()));
  }

  private static class Settings {

    static final String COMMAND = "command";
    static final String PERMISSION = "permission";
  }

  protected class SimpleInventory extends FastInv {

    private final Player player;
    private final int maxSlots;
    private BukkitTask task;
    private boolean forced = false;
    private long ticks = 0;

    public SimpleInventory(Player player, int size, String title) {
      super(size, title != null ? title : InventoryType.CHEST.getDefaultTitle());
      this.maxSlots = size;
      this.player = player;
      setTicks();
      setCloseRequirement();
      createItems();
    }

    public SimpleInventory(Player player, InventoryType type, String title) {
      super(type, title != null ? title : type.getDefaultTitle());
      this.player = player;
      this.maxSlots = type.getDefaultSize();
      setTicks();
      setCloseRequirement();
      createItems();
    }

    public SimpleInventory(Player player, int size) {
      super(size);
      this.player = player;
      this.maxSlots = size;
      setTicks();
      setCloseRequirement();
      createItems();
    }

    public SimpleInventory(Player player, InventoryType type) {
      super(type);
      this.player = player;
      this.maxSlots = type.getDefaultSize();
      setTicks();
      setCloseRequirement();
      createItems();
    }

    private void setTicks() {
      if (menuTicks != null) {
        ticks = menuTicks.getParsed(player);
      }
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
      inventoryMap.put(player.getUniqueId(), this);
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

    private void setCloseRequirement() {
      if (closeRequirement != null) {
        this.setCloseFilter(player1 -> {
          if (!forced) {
            if (!closeRequirement.check(player1)) {
              closeRequirement.sendFailCommand(player1);
              return true;
            }
            closeRequirement.getCheckedRequirement(player1).ifPresent(iconRequirementSet -> {
              iconRequirementSet.take(player1);
              iconRequirementSet.sendSuccessCommands(player1);
            });
          }
          return false;
        });
      }
    }

    private void createDefaultItem(int slot) {
      if (defaultIcon != null) {
        Optional<ClickableItem> rawDefaultClickableItem = defaultIcon
            .createClickableItem(player);
        if (rawDefaultClickableItem.isPresent()) {
          ClickableItem clickableItem = rawDefaultClickableItem.get();
          setItem(slot, clickableItem.getItem(), clickableItem.getClickEvent());
        }
      }
    }

    private void updateDefaultItem(int slot) {
      if (defaultIcon != null) {
        Optional<ClickableItem> rawDefaultClickableItem = defaultIcon
            .updateClickableItem(player);
        if (rawDefaultClickableItem.isPresent()) {
          ClickableItem clickableItem = rawDefaultClickableItem.get();
          setItem(slot, clickableItem.getItem(), clickableItem.getClickEvent());
        }
      }
    }

    private void createItems() {
      for (int i = 0; i < maxSlots; i++) {
        if (icons.containsKey(i)) {
          Optional<ClickableItem> rawClickableItem = icons.get(i).createClickableItem(player);
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
        if (icons.containsKey(i)) {
          Optional<ClickableItem> rawClickableItem = icons.get(i).updateClickableItem(player);
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

    public void forceClose() {
      forced = true;
      player.closeInventory();
    }

    public void open() {
      open(player);
    }
  }
}
