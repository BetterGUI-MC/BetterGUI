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
import me.hsgamer.bettergui.builder.PropertyBuilder;
import me.hsgamer.bettergui.config.impl.MessageConfig;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.icon.DummyIcon;
import me.hsgamer.bettergui.object.property.menu.MenuInventoryType;
import me.hsgamer.bettergui.object.property.menu.MenuRows;
import me.hsgamer.bettergui.object.property.menu.MenuTitle;
import me.hsgamer.bettergui.object.property.menu.MenuVariable;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.permissions.Permission;

public class DummyMenu extends Menu<FastInv> {

  private final Map<UUID, FastInv> inventoryMap = new ConcurrentHashMap<>();

  private final Map<String, DummyIcon> icons = new LinkedHashMap<>();
  private Permission permission = new Permission(
      getInstance().getName().toLowerCase() + "." + getName());

  private MenuInventoryType menuInventoryType;
  private MenuRows menuRows;
  private MenuTitle menuTitle;

  public DummyMenu(String name) {
    super(name);
  }

  @Override
  public void setFromFile(FileConfiguration file) {
    for (String key : file.getKeys(false)) {
      if (key.equalsIgnoreCase("menu-settings")) {
        ConfigurationSection settingsSection = file.getConfigurationSection(key);

        PropertyBuilder.loadMenuPropertiesFromSection(this, settingsSection).values()
            .forEach(menuProperty -> {
              if (menuProperty instanceof MenuInventoryType) {
                this.menuInventoryType = (MenuInventoryType) menuProperty;
              } else if (menuProperty instanceof MenuRows) {
                this.menuRows = (MenuRows) menuProperty;
              } else if (menuProperty instanceof MenuTitle) {
                this.menuTitle = (MenuTitle) menuProperty;
              } else if (menuProperty instanceof MenuVariable) {
                ((MenuVariable) menuProperty).getParsed(null).forEach(menuLocalVariable -> this
                    .registerVariable(menuLocalVariable.getIdentifier(), menuLocalVariable));
              }
            });

        Map<String, Object> keys = new CaseInsensitiveStringMap<>(settingsSection.getValues(false));

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

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Override
  public boolean createInventory(Player player, String[] args, boolean bypass) {
    if (bypass || player.hasPermission(permission)) {
      FastInv inventory = initInventory(player);
      icons.values()
          .forEach(icon -> inventory.addItem(icon.createClickableItem(player).get().getItem()));
      inventory.addCloseHandler(event -> inventoryMap.remove(event.getPlayer().getUniqueId()));
      inventory.open(player);
      inventoryMap.put(player.getUniqueId(), inventory);
      return true;
    } else {
      CommonUtils.sendMessage(player, MessageConfig.NO_PERMISSION.getValue());
      return false;
    }
  }

  private FastInv initInventory(Player player) {
    FastInv inventory;
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
    return inventory;
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

    static final String PERMISSION = "permission";
  }
}
