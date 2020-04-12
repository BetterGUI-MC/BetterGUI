package me.hsgamer.bettergui.object.property.icon.impl;

import java.util.Optional;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.LocalVariable;
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
    getValue().getValues(false).forEach((prefix, object) -> {
      String parsed = String.valueOf(object);
      getIcon().registerVariable(prefix, new LocalVariable<Icon>() {
        @Override
        public String getIdentifier() {
          return prefix;
        }

        @Override
        public Optional<Icon> getInvolved() {
          return Optional.of(getIcon());
        }

        @Override
        public String getReplacement(Player executor, String identifier) {
          return parsed;
        }
      });
    });
  }
}
