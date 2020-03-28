package me.hsgamer.bettergui.object.menu;

import static me.hsgamer.bettergui.BetterGUI.getInstance;
import static me.hsgamer.bettergui.BetterGUI.newChain;

import co.aikar.taskchain.TaskChain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import me.hsgamer.bettergui.builder.CommandBuilder;
import me.hsgamer.bettergui.builder.IconBuilder;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.manager.VariableManager;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.MenuRequirement;
import me.hsgamer.bettergui.object.ParentIcon;
import me.hsgamer.bettergui.object.inventory.SimpleInventory;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.TestCase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.permissions.Permission;

public class SimpleMenu extends Menu {

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
  private MenuRequirement menuRequirement;

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
          menuRequirement = new MenuRequirement(
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
    TestCase.create(player)
        .setPredicate(player1 -> bypass || player1.hasPermission(permission))
        .setSuccessConsumer(player1 -> {
          // Check Requirement
          if (!bypass && menuRequirement != null) {
            if (!menuRequirement.check(player)) {
              menuRequirement.sendFailCommand(player);
              return;
            }
            menuRequirement.getCheckedRequirement(player).ifPresent(iconRequirementSet -> {
              iconRequirementSet.take(player);
              iconRequirementSet.sendCommand(player);
            });
          }

          // Create Inventory
          final SimpleInventory[] inventory = new SimpleInventory[1];
          String parsedTitle = CommonUtils
              .colorize(titleHasVariable ? VariableManager.setVariables(title, player1)
                  : title);
          TestCase.create(inventoryType)
              .setPredicate(inventoryType1 -> inventoryType1.equals(InventoryType.CHEST))
              .setSuccessConsumer(inventoryType1 -> {
                if (parsedTitle != null) {
                  inventory[0] = new SimpleInventory(player1, maxSlots, parsedTitle, icons,
                      defaultIcon, ticks);
                } else {
                  inventory[0] = new SimpleInventory(player1, maxSlots, icons, defaultIcon, ticks);
                }
              })
              .setFailConsumer(inventoryType1 -> {
                if (parsedTitle != null) {
                  inventory[0] = new SimpleInventory(player1, inventoryType1, maxSlots, parsedTitle,
                      icons,
                      defaultIcon,
                      ticks);
                } else {
                  inventory[0] = new SimpleInventory(player1, inventoryType1, maxSlots, icons,
                      defaultIcon,
                      ticks);
                }
              })
              .test();

          // Add Actions
          if (!openActions.isEmpty()) {
            inventory[0].addOpenHandler(event -> {
              TaskChain<?> taskChain = newChain();
              openActions.forEach(action -> action.addToTaskChain(player, taskChain));
              taskChain.execute();
            });
          }
          if (!closeActions.isEmpty()) {
            inventory[0].addCloseHandler(event -> {
              TaskChain<?> taskChain = newChain();
              closeActions.forEach(action -> action.addToTaskChain(player, taskChain));
              taskChain.execute();
            });
          }
          inventory[0].open();
        })
        .setFailConsumer(player1 -> CommonUtils
            .sendMessage(player1,
                getInstance().getMessageConfig().get(DefaultMessage.NO_PERMISSION)))
        .test();
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
}
