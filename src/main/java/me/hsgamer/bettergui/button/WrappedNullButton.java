package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.minecraft.gui.button.impl.NullButton;
import me.hsgamer.hscore.minecraft.gui.event.ClickEvent;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class WrappedNullButton extends ActionButton<NullButton> {
  public WrappedNullButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected Function<Consumer<ClickEvent>, NullButton> getButtonFunction(Map<String, Object> section) {
    return NullButton::new;
  }
}
