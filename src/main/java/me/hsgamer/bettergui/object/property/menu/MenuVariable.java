package me.hsgamer.bettergui.object.property.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.hsgamer.bettergui.object.LocalVariable;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.property.MenuProperty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class MenuVariable extends MenuProperty<ConfigurationSection, List<LocalVariable<Menu<?>>>> {

  public MenuVariable(Menu<?> menu) {
    super(menu);
  }

  @Override
  public List<LocalVariable<Menu<?>>> getParsed(Player player) {
    List<LocalVariable<Menu<?>>> list = new ArrayList<>();
    getValue().getValues(false).forEach((prefix, object) -> {
      String parsed = String.valueOf(object);
      list.add(new LocalVariable<Menu<?>>() {
        @Override
        public String getIdentifier() {
          return prefix;
        }

        @Override
        public Optional<Menu<?>> getInvolved() {
          return Optional.of(getMenu());
        }

        @Override
        public String getReplacement(Player executor, String identifier) {
          return parsed;
        }
      });
    });
    return list;
  }
}
