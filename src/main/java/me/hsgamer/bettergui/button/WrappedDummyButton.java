package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.button.impl.DummyButton;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;

import java.util.Map;

public class WrappedDummyButton extends BaseWrappedButton {
  public WrappedDummyButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected Button createButton(Map<String, Object> section) {
    ItemBuilder itemBuilder = StringReplacerApplier.apply(new ItemBuilder(), this);
    ItemModifierBuilder.INSTANCE.build(section).forEach(itemBuilder::addItemModifier);
    return new DummyButton(itemBuilder::build);
  }
}
