package me.hsgamer.bettergui.menu;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.button.DummyButton;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.utils.CommonStringReplacers;
import me.hsgamer.bettergui.utils.SlotUtils;
import me.hsgamer.hscore.bukkit.gui.GUIDisplay;
import me.hsgamer.hscore.bukkit.gui.GUIHolder;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.hscore.common.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.permissions.Permission;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

public class DummyMenu extends Menu {
  private final Map<String, DummyButton> buttons = new HashMap<>();
  private final GUIHolder guiHolder;
  private Permission permission = new Permission(getInstance().getName().toLowerCase() + "." + getName());

  /**
   * Create a new menu
   *
   * @param name the name of the menu
   */
  public DummyMenu(String name) {
    super(name);
    guiHolder = new GUIHolder(getInstance());
    guiHolder.init();
  }

  public Map<String, DummyButton> getButtons() {
    return buttons;
  }

  @Override
  public void setFromFile(FileConfiguration file) {
    for (Map.Entry<String, Object> entry : file.getValues(false).entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (!(value instanceof ConfigurationSection)) {
        continue;
      }
      ConfigurationSection section = (ConfigurationSection) value;
      Map<String, Object> values = new CaseInsensitiveStringHashMap<>(section.getValues(false));

      if (key.equalsIgnoreCase("menu-settings")) {
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

        this.permission = Optional.ofNullable(values.get("permission")).map(String::valueOf).map(Permission::new).orElse(this.permission);

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
      } else {
        DummyButton button = new DummyButton(this);
        button.setName("menu_" + getName() + "_button_" + key);
        button.setFromSection(section);
        button.init();
        buttons.put(key, button);
        SlotUtils.getSlots(values).forEach(slot -> guiHolder.setButton(slot, button));
      }
    }
  }

  @Override
  public boolean createInventory(Player player, String[] args, boolean bypass) {
    if (bypass || player.hasPermission(permission)) {
      guiHolder.createDisplay(player.getUniqueId()).init();
      return true;
    } else {
      MessageUtils.sendMessage(player, MessageConfig.NO_PERMISSION.getValue());
      return false;
    }
  }

  @Override
  public void updateInventory(Player player) {
    guiHolder.getDisplay(player.getUniqueId()).ifPresent(GUIDisplay::update);
  }

  @Override
  public void closeInventory(Player player) {
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

  public void setPermission(Permission permission) {
    this.permission = permission;
  }
}
