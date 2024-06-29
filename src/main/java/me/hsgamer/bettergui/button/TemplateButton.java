package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.config.TemplateConfig;
import me.hsgamer.hscore.minecraft.gui.button.Button;

import java.util.Map;

public class TemplateButton extends BaseWrappedButton<Button> {
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
  protected Button createButton(Map<String, Object> section) {
    BetterGUI betterGUI = BetterGUI.getInstance();
    finalOptions = betterGUI.get(TemplateConfig.class).getValues(section, "type");
    return ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(getMenu(), getName(), finalOptions)).orElse(null);
  }

  @Override
  public Map<String, Object> getOptions() {
    return finalOptions;
  }
}
