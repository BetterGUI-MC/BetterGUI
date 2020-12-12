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
    Map<String, Object> templateMap = Optional.ofNullable(keys.get("template"))
      .map(String::valueOf)
      .map(s -> BetterGUI.getInstance().getTemplateButtonConfig().get(s))
      .filter(o -> o instanceof ConfigurationSection)
      .map(o -> ((ConfigurationSection) o).getValues(false))
      .orElseGet(CaseInsensitiveStringHashMap::new);
    keys.entrySet()
      .stream()
      .filter(entry -> entry.getKey().equalsIgnoreCase("type") || entry.getKey().equalsIgnoreCase("template"))
      .forEach(entry -> templateMap.put(entry.getKey(), entry.getValue()));

    return ButtonBuilder.INSTANCE.getButton(getMenu(), getName(), section.getRoot().createSection(getName(), templateMap));
  }
}
