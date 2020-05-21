package me.hsgamer.bettergui.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import me.hsgamer.bettergui.manager.MenuManager;
import me.hsgamer.bettergui.manager.VariableManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class Menu<T> implements LocalVariableManager<Menu<?>> {

  private final String name;
  private final Map<String, LocalVariable> variables = new HashMap<>();
  private final Map<UUID, Menu<?>> parentMenu = new HashMap<>();

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
   * @param args   the arguments from the open command
   * @param bypass whether the plugin ignores the permission check
   * @return Whether it's successful
   */
  public abstract boolean createInventory(Player player, String[] args, boolean bypass);

  /**
   * Called when updating the menu
   *
   * @param player the player involved in
   */
  public abstract void updateInventory(Player player);

  /**
   * Close the inventory
   *
   * @param player the player involved in
   */
  public abstract void closeInventory(Player player);

  /**
   * Close/Clear all inventories of the type
   */
  public abstract void closeAll();

  /**
   * Get the player's inventory
   *
   * @param player the player
   * @return the player's inventory
   */
  @SuppressWarnings("unused")
  public abstract Optional<T> getInventory(Player player);

  /**
   * Get the former menu that opened this menu
   *
   * @param player the player
   * @return the former menu
   */
  public Optional<Menu<?>> getParentMenu(Player player) {
    return Optional.ofNullable(parentMenu.get(player.getUniqueId()));
  }

  /**
   * Set the former menu
   *
   * @param player the player
   * @param menu   the former menu
   */
  public void setParentMenu(Player player, Menu<?> menu) {
    parentMenu.put(player.getUniqueId(), menu);
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
  public boolean hasLocalVariables(OfflinePlayer player, String message, boolean checkParent) {
    if (checkParent && player.isOnline()) {
      for (Menu<?> menu : MenuManager.getAllParentMenu(this, player.getPlayer())) {
        if (menu.hasLocalVariables(player, message, false)) {
          return true;
        }
      }
    }
    return VariableManager.isMatch(message, variables.keySet());
  }

  @Override
  public String setSingleVariables(String message, OfflinePlayer executor, boolean checkParent) {
    message = setLocalVariables(message, executor, variables);

    if (checkParent && executor.isOnline()) {
      for (Menu<?> menu : MenuManager.getAllParentMenu(this, executor.getPlayer())) {
        message = menu.setSingleVariables(message, executor, false);
      }
    }

    return message;
  }
}
