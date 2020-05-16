package me.hsgamer.bettergui.object.property.icon.impl;

import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.LocalVariable;
import me.hsgamer.bettergui.object.LocalVariableManager;
import me.hsgamer.bettergui.object.property.IconProperty;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

public class Variable extends IconProperty<ConfigurationSection> {

  public Variable(Icon icon) {
    super(icon);
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    getValue().getValues(false).forEach((prefix, object) -> {
      String parsed = String.valueOf(object);
      getIcon().registerVariable(prefix, new LocalVariable() {
        @Override
        public String getIdentifier() {
          return prefix;
        }

        @Override
        public LocalVariableManager<?> getInvolved() {
          return getIcon();
        }

        @Override
        public String getReplacement(OfflinePlayer executor, String identifier) {
          return parsed;
        }
      });
    });
  }
}
