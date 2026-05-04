package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.MenuButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.config.TemplateConfig;
import me.hsgamer.hscore.common.StringReplacer;

import java.util.Map;

public class TemplateButton extends MenuButton {
  private Map<String, Object> finalOptions;

  /**
   * Create a new button
   *
   * @param input the input
   */
  public TemplateButton(ButtonBuilder.Input input) {
    super(input);
    finalOptions = input.options;
  }

  @Override
  protected MenuButton createButton(Map<String, Object> section) {
    finalOptions = BetterGUI.getInstance().get(TemplateConfig.class).getValues(section, "type");
    return BetterGUI.getInstance().get(ButtonBuilder.class).build(new ButtonBuilder.Input(this, getName(), finalOptions)).orElse(null);
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
