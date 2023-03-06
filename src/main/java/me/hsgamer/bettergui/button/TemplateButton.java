package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.minecraft.gui.button.Button;

import java.util.Map;

public class TemplateButton extends BaseWrappedButton<Button> {
  /**
   * Create a new button
   *
   * @param input the input
   */
  public TemplateButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected Button createButton(Map<String, Object> section) {
    Map<String, Object> finalMap = BetterGUI.getInstance().getTemplateButtonConfig().getValues(section, "type");
    return ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(getMenu(), getName(), finalMap)).orElse(null);
  }
}
