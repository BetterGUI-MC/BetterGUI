package me.hsgamer.bettergui.button;

import io.github.projectunified.craftux.common.Button;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;

import java.util.Map;

public class EmptyButton extends BaseWrappedButton<Button> {
  public EmptyButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected Button createButton(Map<String, Object> section) {
    return (uuid, actionItem) -> true;
  }
}
