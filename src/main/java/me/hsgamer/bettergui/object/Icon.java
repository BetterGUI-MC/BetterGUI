package me.hsgamer.bettergui.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.manager.VariableManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public abstract class Icon implements Cloneable {

  private static final Pattern pattern = Pattern.compile("[{]([^{}]+)[}]");
  private Map<String, IconVariable> variables = new HashMap<>();
  private String name;
  private Menu menu;

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

  public void registerVariable(SimpleIconVariable variable) {
    variables.put(variable.getIdentifier(), variable);
  }

  public void registerVariable(String identifier, IconVariable variable) {
    variables.put(identifier, variable);
  }

  public boolean hasVariables(String message) {
    if (message == null) {
      return false;
    }
    if (VariableManager.hasVariables(message)) {
      return true;
    }
    Pattern prefixPattern = Pattern.compile("(" + String.join("|", variables.keySet()) + ").*");
    Matcher matcher = pattern.matcher(message);
    while (matcher.find()) {
      String identifier = matcher.group(1).trim();
      if (prefixPattern.matcher(identifier).find()) {
        return true;
      }
    }
    return false;
  }

  public String setVariables(String message, Player executor) {
    message = VariableManager.setVariables(message, executor);
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

  public abstract void setFromSection(ConfigurationSection section);

  public abstract Optional<ClickableItem> createClickableItem(Player player);

  public abstract Optional<ClickableItem> updateClickableItem(Player player);

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
