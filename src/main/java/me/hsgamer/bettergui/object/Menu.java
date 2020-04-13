package me.hsgamer.bettergui.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import me.hsgamer.bettergui.manager.VariableManager;
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class Menu<T> implements LocalVariableManager<Menu<?>> {

  private static final Pattern pattern = Pattern.compile("[{]([^{}]+)[}]");
  private final String name;
  private final Map<String, LocalVariable> variables = new HashMap<>();
  private Menu<?> parentMenu;

  public Menu(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  /**
   * Called when setting options
   *
   * @param file the file of the menu
   */
  public abstract void setFromFile(FileConfiguration file);

  /**
   * Called when opening the menu for the player
   *
   * @param player the player involved in
   * @param bypass whether the plugin ignores the permission check
   */
  public abstract void createInventory(Player player, boolean bypass);

  public abstract void updateInventory(Player player);

  public abstract void closeInventory(Player player);

  public abstract void closeAll();

  @SuppressWarnings("unused")
  public abstract Optional<T> getInventory(Player player);

  /**
   * Get the former menu that opened this menu
   *
   * @return the former menu
   */
  public Optional<Menu<?>> getParentMenu() {
    return Optional.ofNullable(parentMenu);
  }

  /**
   * Set the former menu
   *
   * @param parentMenu the former menu
   */
  public void setParentMenu(Menu<?> parentMenu) {
    this.parentMenu = parentMenu;
  }

  @Override
  public void registerVariable(String identifier, LocalVariable variable) {
    variables.put(identifier, variable);
  }

  @Override
  public Menu<?> getParent() {
    return this;
  }

  @Override
  public boolean hasVariables(String message) {
    if (message == null || message.trim().isEmpty()) {
      return false;
    }
    if (VariableManager.hasVariables(message) || (parentMenu != null && parentMenu
        .hasVariables(message))) {
      return true;
    }
    return Validate.isMatch(message, pattern, variables.keySet());
  }

  @Override
  public String setSingleVariables(String message, Player executor) {
    message = setLocalVariables(message, executor, pattern, variables);
    if (parentMenu != null) {
      message = parentMenu.setSingleVariables(message, executor);
    }
    return message;
  }
}
