package me.hsgamer.bettergui.object.menu;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import me.hsgamer.bettergui.builder.CommandBuilder;
import me.hsgamer.bettergui.builder.IconBuilder;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.ParentIcon;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.permissions.Permission;

public class SimpleMenu extends Menu {

  private InventoryType inventoryType = InventoryType.CHEST;
  private String title;
  private int maxSlots = 27;
  private Map<Integer, Icon> icons = new HashMap<>();
  private List<Command> openActions = new ArrayList<>();
  private List<Command> closeActions = new ArrayList<>();
  private Permission permission = new Permission(
      getInstance().getName().toLowerCase() + "." + getName());
  private Icon defaultIcon;

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
          title = (String) keys.get(Settings.NAME);
        }

        if (keys.containsKey(Settings.INVENTORY_TYPE)) {
          try {
            inventoryType = InventoryType.valueOf((String) keys.get(Settings.INVENTORY_TYPE));
          } catch (IllegalArgumentException e) {
            getInstance().getLogger().log(Level.WARNING, "The menu \"" + file.getName()
                + "\" contains an illegal inventory type, it will be CHEST by default");
          }
          switch (inventoryType) {
            case FURNACE:
              maxSlots = 3;
              break;
            case ENDER_CHEST:
            case CHEST:
              maxSlots = 27;
              break;
            case HOPPER:
              maxSlots = 5;
              break;
            case WORKBENCH:
              maxSlots = 10;
              break;
            case DISPENSER:
            case DROPPER:
              maxSlots = 9;
              break;
            default:
              inventoryType = InventoryType.CHEST;
              getInstance().getLogger().log(Level.WARNING, "The menu \"" + file.getName()
                  + "\"'s inventory type is not supported, it will be CHEST by default");
          }
        } else if (keys.containsKey(Settings.ROW)) {
          int temp = (int) keys.get(Settings.ROW) * 9;
          maxSlots = temp > 0 ? temp : maxSlots;
        }

        if (keys.containsKey(Settings.COMMAND)) {
          Object value = keys.get(Settings.COMMAND);
          List<String> commands = new ArrayList<>();
          if (value instanceof List) {
            commands = (List<String>) value;
          } else if (value instanceof String) {
            commands = Arrays.asList(((String) value).split(";"));
          }
          commands.replaceAll(String::trim);
          commands.forEach(s -> getInstance().getCommandManager().registerMenuCommand(s, this));
        }

        if (keys.containsKey(Settings.OPEN_ACTION)) {
          Object value = keys.get(Settings.OPEN_ACTION);
          if (value instanceof List) {
            openActions.addAll(CommandBuilder.getCommands(null, (List<String>) value));
          } else if (value instanceof String) {
            openActions.addAll(CommandBuilder.getCommands(null, (String) value));
          }
        }
        if (keys.containsKey(Settings.CLOSE_ACTION)) {
          Object value = keys.get(Settings.CLOSE_ACTION);
          if (value instanceof List) {
            closeActions.addAll(CommandBuilder.getCommands(null, (List<String>) value));
          } else if (value instanceof String) {
            closeActions.addAll(CommandBuilder.getCommands(null, (String) value));
          }
        }

        if (keys.containsKey(Settings.PERMISSION)) {
          permission = new Permission((String) keys.get(Settings.PERMISSION));
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
              ((ParentIcon) tempIcon).addChild(icon);
            } else {
              // TODO: Config, Override icon
            }
          } else {
            if (slot <= maxSlots) {
              icons.put(slot, icon);
            } else {
              // TODO: Config, Invalid slot
            }
          }
        }
      }
    }
  }

  // TODO: Finish up
  @Override
  public void createInventory(Player player) {

  }

  public Icon getDefaultIcon() {
    return defaultIcon;
  }

  private static class Settings {

    static final String NAME = "name";
    static final String ROW = "row";
    static final String INVENTORY_TYPE = "inventory-type";
    static final String COMMAND = "command";
    static final String OPEN_ACTION = "open-action";
    static final String CLOSE_ACTION = "close-action";
    static final String PERMISSION = "permission";
  }
}
