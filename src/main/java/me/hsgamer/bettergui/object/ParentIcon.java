package me.hsgamer.bettergui.object;

import me.hsgamer.bettergui.builder.IconBuilder;
import org.bukkit.configuration.ConfigurationSection;

public interface ParentIcon {

  default void setChildFromSection(Menu menu, ConfigurationSection section) {
    for (String key : section.getKeys(false)) {
      if (key.equalsIgnoreCase("child")) {
        ConfigurationSection subsection = section.getConfigurationSection(key);
        subsection.getKeys(false).forEach(
            name -> addChild(IconBuilder.getIcon(menu, subsection.getConfigurationSection(name))));
        break;
      }
    }
  }

  void addChild(Icon icon);
}
