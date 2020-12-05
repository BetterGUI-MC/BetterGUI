package me.hsgamer.bettergui.api.button;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.bukkit.gui.Button;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.UUID;

/**
 * The base wrapped button
 */
public abstract class BaseWrappedButton implements WrappedButton {
  private final Menu menu;
  private Button button;
  private String name = "";

  /**
   * Create a new button
   *
   * @param menu the menu
   */
  public BaseWrappedButton(Menu menu) {
    this.menu = menu;
  }

  /**
   * Create the button from the section
   *
   * @param section the section
   *
   * @return the button
   */
  protected abstract Button createButton(ConfigurationSection section);

  @Override
  public void setFromSection(ConfigurationSection section) {
    this.button = createButton(section);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
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
