package me.hsgamer.bettergui.object.property.icon;

import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.IconVariable;
import me.hsgamer.bettergui.object.property.IconProperty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Variable extends IconProperty<ConfigurationSection> {

  public Variable(Icon icon) {
    super(icon);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    for (String prefix : getValue().getKeys(false)) {
      String parsed = getValue().getString(prefix);
      getIcon().registerVariable(prefix, new IconVariable(getIcon()) {
        @Override
        public String getReplacement(Player player, String identifier) {
          return parsed;
        }
      });
    }
  }
}
