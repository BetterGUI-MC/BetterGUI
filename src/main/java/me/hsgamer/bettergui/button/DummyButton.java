package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.utils.CommonStringReplacers;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.UUID;

public class DummyButton implements WrappedButton {
  private final Menu menu;
  private final ItemBuilder itemBuilder = new ItemBuilder()
    .addStringReplacer("variable", CommonStringReplacers.VARIABLE)
    .addStringReplacer("colorize", CommonStringReplacers.COLORIZE)
    .addStringReplacer("expression", CommonStringReplacers.EXPRESSION);
  private String name;

  /**
   * Create a new button
   *
   * @param menu the menu
   */
  public DummyButton(Menu menu) {
    this.menu = menu;
  }

  @Override
  public void setFromSection(ConfigurationSection section) {
    ItemModifierBuilder.INSTANCE.getItemModifiers(section).forEach(itemBuilder::addItemModifier);
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
    return itemBuilder.build(uuid);
  }

  @Override
  public void handleAction(UUID uuid, InventoryClickEvent event) {
    // EMPTY
  }

  @Override
  public void init() {
    // EMPTY
  }

  @Override
  public void stop() {
    // EMPTY
  }
}
