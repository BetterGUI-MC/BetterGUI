package me.hsgamer.bettergui.object.property.icon;

import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.SimpleIconVariable;
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
      getIcon().registerVariable(new SimpleIconVariable(getIcon()) {
        @Override
        public String getIdentifier() {
          return prefix;
        }

        @Override
        public String getReplacement(Player player, String identifier) {
          return parsed;
        }
      });
    }
  }
}
