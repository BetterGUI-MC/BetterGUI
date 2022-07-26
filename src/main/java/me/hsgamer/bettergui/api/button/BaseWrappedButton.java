package me.hsgamer.bettergui.api.button;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

/**
 * The base class of wrapped button
 */
public abstract class BaseWrappedButton implements WrappedButton {
  protected final Menu menu;
  protected final String name;
  protected final Map<String, Object> options;
  protected final Button button;

  /**
   * Create a new wrapped button
   *
   * @param input the input
   */
  protected BaseWrappedButton(ButtonBuilder.Input input) {
    this.menu = input.menu;
    this.name = input.name;
    this.options = input.options;
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
   * Get the options of the button
   *
   * @return the options
   */
  public Map<String, Object> getOptions() {
    return options;
  }

  @Override
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
