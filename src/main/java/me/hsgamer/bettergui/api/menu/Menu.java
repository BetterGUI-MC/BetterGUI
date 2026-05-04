package me.hsgamer.bettergui.api.menu;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.element.MenuElement;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.variable.VariableManager;
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
  protected final VariableManager variableManager = new VariableManager();
  private final Map<UUID, Menu> parentMenu = new HashMap<>();

  /**
   * Create a new menu
   *
   * @param config the config
   */
  protected Menu(Config config) {
    this.config = config;
    variableManager.register("current-menu", original -> getName(), true);
    variableManager.register("parent-menu", StringReplacer.of((original, uuid) -> getParentMenu(uuid).map(Menu::getName).orElse("")));
    variableManager.addExternalReplacer(BetterGUI.getInstance().get(VariableManager.class));
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

  /**
   * Get the variable manager of the menu
   *
   * @return the variable manager
   */
  @Deprecated
  public VariableManager getVariableManager() {
    return variableManager;
  }

  @Override
  public @Nullable String replace(@NotNull String original) {
    return variableManager.replace(original);
  }

  @Override
  public @Nullable String replace(@NotNull String original, @NotNull UUID uuid) {
    return variableManager.replace(original, uuid);
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
}
