package me.hsgamer.bettergui.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.manager.VariableManager;
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public abstract class Icon implements Cloneable {

  private static final Pattern pattern = Pattern.compile("[{]([^{}]+)[}]");
  private final String name;
  private final Menu menu;
  private Map<String, IconVariable> variables = new HashMap<>();

  public Icon(String name, Menu menu) {
    this.name = name;
    this.menu = menu;
  }

  public Icon(Icon original) {
    this.variables = original.variables;
    this.name = original.name;
    this.menu = original.menu;
  }

  public String getName() {
    return name;
  }

  /**
   * Register a simple icon-only variable
   *
   * @param variable the variable
   */
  public void registerVariable(SimpleIconVariable variable) {
    variables.put(variable.getIdentifier(), variable);
  }

  /**
   * Register new icon-only variable
   *
   * @param identifier the variable
   * @param variable   the IconVariable object
   */
  public void registerVariable(String identifier, IconVariable variable) {
    variables.put(identifier, variable);
  }


  /**
   * Check if the string contains variables
   *
   * @param message the string
   * @return true if it has, otherwise false
   */
  public boolean hasVariables(String message) {
    if (message == null || message.trim().isEmpty()) {
      return false;
    }
    if (VariableManager.hasVariables(message)) {
      return true;
    }
    return Validate.isMatch(message, pattern, variables.keySet());
  }

  /**
   * Replace the variables of the string
   *
   * @param message  the string
   * @param executor the player involved in
   * @return the replaced string
   */
  public String setVariables(String message, Player executor) {
    String old;
    do {
      old = message;
      message = setSingleVariables(message, executor);
    } while (hasVariables(message) && !old.equals(message));
    return VariableManager.setVariables(message, executor);
  }

  private String setSingleVariables(String message, Player executor) {
    Matcher matcher = pattern.matcher(message);
    while (matcher.find()) {
      String identifier = matcher.group(1).trim();
      for (Map.Entry<String, IconVariable> variable : variables.entrySet()) {
        if (identifier.startsWith(variable.getKey())) {
          String replace = variable.getValue()
              .getReplacement(executor, identifier.substring(variable.getKey().length()));
          if (replace != null) {
            message = message
                .replaceAll(Pattern.quote(matcher.group()), Matcher.quoteReplacement(replace));
          }
        }
      }
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
  public Menu getMenu() {
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
