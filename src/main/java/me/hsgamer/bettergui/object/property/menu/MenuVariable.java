package me.hsgamer.bettergui.object.property.menu;

import java.util.ArrayList;
import java.util.List;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.property.MenuProperty;
import me.hsgamer.bettergui.object.variable.LocalVariable;
import me.hsgamer.bettergui.object.variable.LocalVariableManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class MenuVariable extends MenuProperty<ConfigurationSection, List<LocalVariable>> {

  public MenuVariable(Menu<?> menu) {
    super(menu);
  }

  @Override
  public List<LocalVariable> getParsed(Player player) {
    List<LocalVariable> list = new ArrayList<>();
    getValue().getValues(false).forEach((prefix, object) -> {
      String parsed = String.valueOf(object);
      list.add(new LocalVariable() {
        @Override
        public String getIdentifier() {
          return prefix;
        }

        @Override
        public LocalVariableManager<?> getInvolved() {
          return getMenu();
        }

        @Override
        public String getReplacement(OfflinePlayer executor, String identifier) {
          return parsed;
        }
      });
    });
    return list;
  }
}
