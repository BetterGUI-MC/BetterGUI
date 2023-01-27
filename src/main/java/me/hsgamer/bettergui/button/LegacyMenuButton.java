package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.minecraft.gui.button.Button;
import me.hsgamer.hscore.minecraft.gui.button.impl.PredicateButton;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class LegacyMenuButton extends BaseWrappedButton<PredicateButton> {
  private Set<UUID> checked;

  public LegacyMenuButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected PredicateButton createButton(Map<String, Object> section) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);
    WrappedSimpleButton simpleButton = new WrappedSimpleButton(new ButtonBuilder.Input(getMenu(), getName(), section));
    PredicateButton predicateButton = new PredicateButton().setButton(simpleButton);
    checked = new ConcurrentSkipListSet<>();
    WrappedPredicateButton.applyRequirement(keys, this, checked, predicateButton);
    return predicateButton;
  }

  @Override
  public void refresh(UUID uuid) {
    checked.remove(uuid);
    if (this.button == null) {
      return;
    }
    Button tempButton = this.button.getButton();
    if (tempButton instanceof WrappedButton) {
      ((WrappedButton) tempButton).refresh(uuid);
    }
  }
}
