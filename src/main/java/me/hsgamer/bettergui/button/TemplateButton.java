package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.bukkit.gui.Button;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Optional;

public class TemplateButton extends BaseWrappedButton {
  /**
   * Create a new button
   *
   * @param menu the menu
   */
  public TemplateButton(Menu menu) {
    super(menu);
  }

  @Override
  protected Button createButton(ConfigurationSection section) {
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section.getValues(false));
    ConfigurationSection templateSection = (ConfigurationSection) Optional.ofNullable(keys.get("template"))
      .map(String::valueOf)
      .map(s -> BetterGUI.getInstance().getTemplateButtonConfig().get(s))
      .filter(o -> o instanceof ConfigurationSection).orElse(null);
    if (templateSection == null) {
      return null;
    }

    Map<String, Object> templateMap = templateSection.getValues(false);
    keys.forEach((key, value) -> {
      if (key.equalsIgnoreCase("type") || key.equalsIgnoreCase("template")) {
        return;
      }
      templateMap.put(key, value);
    });

    ConfigurationSection finalSection = section.getRoot().createSection(getName(), templateMap);
    return ButtonBuilder.INSTANCE.getButton(getMenu(), getName(), finalSection);
  }
}
