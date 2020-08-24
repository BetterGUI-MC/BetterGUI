package me.hsgamer.bettergui.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.manager.VariableManager;
import me.hsgamer.bettergui.object.variable.LocalVariable;
import me.hsgamer.bettergui.object.variable.LocalVariableManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public abstract class Icon implements Cloneable, LocalVariableManager<Icon> {

  private final String name;
  private final Menu<?> menu;
  private final Map<String, LocalVariable> variables = new HashMap<>();

  public Icon(String name, Menu<?> menu) {
    this.name = name;
    this.menu = menu;
  }

  public Icon(Icon original) {
    this.variables.putAll(original.variables);
    this.name = original.name;
    this.menu = original.menu;
  }

  public String getName() {
    return name;
  }

  @Override
  public Icon getParent() {
    return this;
  }

  @Override
  public void registerVariable(String identifier, LocalVariable variable) {
    variables.put(identifier, variable);
  }

  @Override
  public boolean hasLocalVariables(OfflinePlayer player, String message, boolean checkParent) {
    if (checkParent && menu.hasLocalVariables(player, message, true)) {
      return true;
    }
    return VariableManager.isMatch(message, variables.keySet());
  }

  @Override
  public String setSingleVariables(String message, OfflinePlayer executor, boolean checkParent) {
    message = setLocalVariables(message, executor, variables);
    if (checkParent) {
      message = menu.setSingleVariables(message, executor);
    }
    return message;
  }

  /**
   * Called when setting options
   *
   * @param section the section of that icon in the config
   */
  public abstract void setFromSection(ConfigurationSection section);

  /**
   * Called when opening the menu containing this icon
   *
   * @param player the player involved in
   * @return a clickable item
   */
  public abstract Optional<ClickableItem> createClickableItem(Player player);

  /**
   * Called when updating the menu containing this icon
   *
   * @param player the player involved in
   * @return a clickable item
   */
  public abstract Optional<ClickableItem> updateClickableItem(Player player);

  /**
   * Get the menu that contains this icon
   *
   * @return the menu
   */
  public Menu<?> getMenu() {
    return menu;
  }

  public Icon cloneIcon() {
    try {
      return getClass().getDeclaredConstructor(Icon.class).newInstance(this);
    } catch (Exception e) {
      BetterGUI.getInstance().getLogger()
          .log(Level.WARNING, "There is a problem when cloning icon", e);
      return this;
    }
  }
}
