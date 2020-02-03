package me.hsgamer.bettergui.object;

import me.hsgamer.bettergui.builder.IconBuilder;
import org.bukkit.configuration.ConfigurationSection;

/**
 * The interface for Icon that supports adding other icons
 */
public interface ParentIcon {

  /**
   * Called when adding child icons from the "child" section in the config
   *
   * @param menu    the menu containing the icon
   * @param section the "child" section
   */
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

  /**
   * Called when adding a child icon to the icon
   *
   * @param icon the child icon
   */
  void addChild(Icon icon);
}
