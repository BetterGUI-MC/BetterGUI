package me.hsgamer.bettergui.api.menu;

import me.hsgamer.hscore.common.interfaces.StringReplacer;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.variable.ExternalStringReplacer;
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
  protected final List<ExternalStringReplacer> stringReplacers = new ArrayList<>();
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

    stringReplacers.add(new ExternalStringReplacer() {
      @Override
      public boolean canBeReplaced(String string) {
        return variableManager.hasVariables(string);
      }

      @Override
      public String replace(String original, UUID uuid) {
        return variableManager.setVariables(original, uuid);
      }
    });
    stringReplacers.add(new ExternalStringReplacer() {
      @Override
      public boolean canBeReplaced(String string) {
        return VariableManager.hasVariables(string);
      }

      @Override
      public String replace(String original, UUID uuid) {
        return VariableManager.setVariables(original, uuid);
      }
    });
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
  public List<ExternalStringReplacer> getStringReplacers() {
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
   * Get the original
   *
   * @return the original
   */
  public abstract Object getOriginal();

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
    parentMenu.put(uuid, menu);
  }

  /**
   * Check if the string can be replaced
   *
   * @param string the string
   *
   * @return true if it can be replaced
   */
  public boolean canBeReplaced(String string) {
    return stringReplacers.stream().anyMatch(replacer -> replacer.canBeReplaced(string));
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
