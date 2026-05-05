package me.hsgamer.bettergui.api.menu;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.element.MenuElement;
import me.hsgamer.bettergui.manager.MenuManager;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * The menu
 */
public abstract class Menu implements MenuElement {
  /**
   * The path of the menu settings
   */
  public static final String MENU_SETTINGS_PATH = "menu-settings";

  protected final Config config;

  /**
   * Create a new menu
   *
   * @param config the config
   */
  protected Menu(Config config) {
    this.config = config;
  }

  /**
   * Get the config
   *
   * @return the config
   */
  public Config getConfig() {
    return config;
  }

  @Override
  public MenuElement getParent() {
    return null;
  }

  @Override
  public String getName() {
    return config.getName();
  }

  @Override
  public StringReplacer getStringReplacer() {
    return new StringReplacer() {
      @Override
      public @Nullable String replace(@NotNull String original) {
        if (original.equalsIgnoreCase("current-menu")) {
          return getName();
        }
        return null;
      }

      @Override
      public @Nullable String replace(@NotNull String original, @NotNull UUID uuid) {
        if (original.equalsIgnoreCase("parent-menu")) {
          return BetterGUI.getInstance().get(MenuManager.class).getParentMenu(uuid, Menu.this).map(Menu::getName).orElse(null);
        }
        return replace(original);
      }
    };
  }

  @Override
  public Menu getMenu() {
    return this;
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
}
