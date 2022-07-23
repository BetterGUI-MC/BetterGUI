package me.hsgamer.bettergui.api.button;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

/**
 * The wrapped button for Menu
 */
public abstract class WrappedButton implements Button, MenuElement {
  protected final Menu menu;
  protected final String name;
  protected final Button button;

  /**
   * Create a new wrapped button
   *
   * @param menu    the menu
   * @param name    the name
   * @param options the options
   */
  protected WrappedButton(Menu menu, String name, Map<String, Object> options) {
    this.menu = menu;
    this.name = name;
    this.button = createButton(options);
  }

  /**
   * Create the button from the section
   *
   * @param section the section
   *
   * @return the button
   */
  protected abstract Button createButton(Map<String, Object> section);

  /**
   * Refresh the button for the unique id
   *
   * @param uuid the unique id
   */
  public void refresh(UUID uuid) {
    // EMPTY
  }

  public String getName() {
    return name;
  }

  @Override
  public Menu getMenu() {
    return menu;
  }

  @Override
  public ItemStack getItemStack(UUID uuid) {
    if (button != null) {
      return button.getItemStack(uuid);
    }
    return null;
  }

  @Override
  public void handleAction(UUID uuid, InventoryClickEvent event) {
    if (button != null) {
      button.handleAction(uuid, event);
    }
  }

  @Override
  public void init() {
    if (button != null) {
      button.init();
    }
  }

  @Override
  public void stop() {
    if (button != null) {
      button.stop();
    }
  }
}
