package me.hsgamer.bettergui.api.menu;

import me.hsgamer.hscore.common.interfaces.StringReplacer;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.variable.ExternalStringReplacer;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * The menu
 */
public abstract class Menu {

  protected final Config config;
  private final Map<UUID, Menu> parentMenu = new HashMap<>();
  private final List<ExternalStringReplacer> stringReplacers = new ArrayList<>();

  /**
   * Create a new menu
   *
   * @param config the config
   */
  protected Menu(Config config) {
    this.config = config;
    stringReplacers.add(new ExternalStringReplacer() {
      private static final String CURRENT_MENU_VARIABLE = "{current-menu}";

      @Override
      public boolean canBeReplaced(String string) {
        return string.contains(CURRENT_MENU_VARIABLE);
      }

      @Override
      public String replace(String original, UUID uuid) {
        return original.replace(CURRENT_MENU_VARIABLE, getName());
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
