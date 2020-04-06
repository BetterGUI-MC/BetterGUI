package me.hsgamer.bettergui.object.menu;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import fr.mrmicky.fastinv.FastInv;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import me.hsgamer.bettergui.builder.IconBuilder;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.manager.VariableManager;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.icon.DummyIcon;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.permissions.Permission;

public class DummyMenu extends Menu<FastInv> {

  protected final Map<UUID, FastInv> inventoryMap = new ConcurrentHashMap<>();

  private final Map<String, DummyIcon> icons = new LinkedHashMap<>();
  private String title;
  private boolean titleHasVariable = false;
  private InventoryType inventoryType = InventoryType.CHEST;
  private int maxSlots = 54;
  private Permission permission = new Permission(
      getInstance().getName().toLowerCase() + "." + getName());

  public DummyMenu(String name) {
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

        if (keys.containsKey(Settings.PERMISSION)) {
          permission = new Permission(String.valueOf(keys.get(Settings.PERMISSION)));
        }
      } else if (!icons.containsKey(key)) {
        icons.put(key,
            IconBuilder.getIcon(this, file.getConfigurationSection(key), DummyIcon.class));
      } else {
        getInstance().getLogger().log(Level.WARNING, "Duplicated icon {0}", key);
      }
    }
  }

  @Override
  public void createInventory(Player player, boolean bypass) {
    if (bypass || player.hasPermission(permission)) {
      FastInv inventory;
      String parsedTitle = CommonUtils
          .colorize(titleHasVariable ? VariableManager.setVariables(title, player) : title);
      if (inventoryType.equals(InventoryType.CHEST)) {
        if (parsedTitle != null) {
          inventory = new FastInv(maxSlots, parsedTitle);
        } else {
          inventory = new FastInv(maxSlots);
        }
      } else {
        if (parsedTitle != null) {
          inventory = new FastInv(inventoryType, parsedTitle);
        } else {
          inventory = new FastInv(inventoryType);
        }
      }
      icons.values()
          .forEach(icon -> inventory.addItem(icon.createClickableItem(player).get().getItem()));
      inventory.addCloseHandler(event -> inventoryMap.remove(event.getPlayer().getUniqueId()));
      inventory.open(player);
      inventoryMap.put(player.getUniqueId(), inventory);
    } else {
      CommonUtils
          .sendMessage(player, getInstance().getMessageConfig().get(DefaultMessage.NO_PERMISSION));
    }
  }

  @Override
  public void updateInventory(Player player) {
    // Ignored
  }

  @Override
  public void closeInventory(Player player) {
    player.closeInventory();
  }

  @Override
  public void closeAll() {
    inventoryMap.keySet().forEach(uuid -> Bukkit.getPlayer(uuid).closeInventory());
    inventoryMap.clear();
  }

  @Override
  public Optional<FastInv> getInventory(Player player) {
    return Optional.ofNullable(inventoryMap.get(player.getUniqueId()));
  }

  public Map<String, DummyIcon> getIcons() {
    return icons;
  }

  public void setPermission(Permission permission) {
    this.permission = permission;
  }

  private static class Settings {

    static final String NAME = "name";
    static final String ROWS = "rows";
    static final String INVENTORY_TYPE = "inventory-type";
    static final String PERMISSION = "permission";
  }
}
