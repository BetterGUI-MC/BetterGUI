package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.MenuButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.config.TemplateConfig;
import me.hsgamer.hscore.common.StringReplacer;

import java.util.Map;

public class TemplateButton extends MenuButton {
  private final BetterGUI plugin;
  private Map<String, Object> finalOptions;

  public TemplateButton(BetterGUI plugin, ButtonBuilder.Input input) {
    super(input);
    this.plugin = plugin;
    finalOptions = input.options;
  }

  @Override
  protected MenuButton createButton(Map<String, Object> section) {
    finalOptions = plugin.get(TemplateConfig.class).getValues(section, "type");
    return plugin.get(ButtonBuilder.class).build(new ButtonBuilder.Input(this, getName(), finalOptions)).orElse(null);
  }

  @Override
  public Map<String, Object> getOptions() {
    return finalOptions;
  }

  @Override
  public StringReplacer getStringReplacer() {
    if (this.button instanceof MenuButton) {
      return ((MenuButton) this.button).getStringReplacer();
    }
    return super.getStringReplacer();
  }
}
