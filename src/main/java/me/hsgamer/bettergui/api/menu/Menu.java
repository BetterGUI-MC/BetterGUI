package me.hsgamer.bettergui.api.menu;

import me.hsgamer.hscore.common.interfaces.StringReplacer;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.variable.InstanceVariableManager;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * The menu
 */
public abstract class Menu {

  protected final Config config;
  protected final InstanceVariableManager variableManager = new InstanceVariableManager();
  protected final List<StringReplacer> stringReplacers = new ArrayList<>();
  private final Map<UUID, Menu> parentMenu = new HashMap<>();

  /**
   * Create a new menu
   *
   * @param config the config
   */
  protected Menu(Config config) {
    this.config = config;
    variableManager.register("current-menu", (original, uuid) -> getName());
    variableManager.register("parent-menu", (original, uuid) -> getParentMenu(uuid).map(Menu::getName).orElse(""));

    stringReplacers.add(variableManager::setVariables);
    stringReplacers.add(VariableManager::setVariables);
  }

  /**
   * Get the name
   *
   * @return the name
   */
  public String getName() {
    return config.getName();
  }

  /**
   * Get the config
   *
   * @return the config
   */
  public Config getConfig() {
    return config;
  }

  /**
   * Get the variable manager
   *
   * @return the variable manager
   */
  public InstanceVariableManager getVariableManager() {
    return variableManager;
  }

  /**
   * Get the mutable string replacers
   *
   * @return the string replacers
   */
  public List<StringReplacer> getStringReplacers() {
    return stringReplacers;
  }

  /**
   * Called when opening the menu for the player
   *
   * @param player the player involved in
   * @param args   the arguments from the open command
   * @param bypass whether the plugin ignores the permission check
   *
   * @return Whether it's successful
   */
  public abstract boolean create(Player player, String[] args, boolean bypass);

  /**
   * Called when the player hit TAB when typing the command to open the menu
   *
   * @param player the player involved in
   * @param args   the arguments from the open command
   *
   * @return the list of suggestions
   */
  public List<String> tabComplete(Player player, String[] args) {
    return Collections.emptyList();
  }

  /**
   * Called when updating the menu
   *
   * @param player the player involved in
   */
  public abstract void update(Player player);

  /**
   * Close the menu
   *
   * @param player the player involved in
   */
  public abstract void close(Player player);

  /**
   * Close/Clear all inventories of the type
   */
  public abstract void closeAll();

  /**
   * Get the former menu that opened this menu
   *
   * @param uuid the unique id
   *
   * @return the former menu
   */
  public Optional<Menu> getParentMenu(UUID uuid) {
    return Optional.ofNullable(parentMenu.get(uuid));
  }

  /**
   * Set the former menu
   *
   * @param uuid the unique id
   * @param menu the former menu
   */
  public void setParentMenu(UUID uuid, Menu menu) {
    if (menu == null) {
      parentMenu.remove(uuid);
    } else {
      parentMenu.put(uuid, menu);
    }
  }

  /**
   * Replace the string
   *
   * @param string the string
   * @param uuid   the unique id
   *
   * @return the replaced string
   */
  public String replace(String string, UUID uuid) {
    return StringReplacer.replace(string, uuid, stringReplacers);
  }
}
