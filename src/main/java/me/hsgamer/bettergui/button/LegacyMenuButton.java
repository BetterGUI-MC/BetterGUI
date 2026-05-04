package me.hsgamer.bettergui.button;

import io.github.projectunified.craftux.button.PredicateButton;
import io.github.projectunified.craftux.common.Button;
import me.hsgamer.bettergui.api.button.MenuButton;
import me.hsgamer.bettergui.api.replacer.LookupStringReplacer;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.common.StringReplacer;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class LegacyMenuButton extends MenuButton {
  private final Set<UUID> checked = new ConcurrentSkipListSet<>();

  public LegacyMenuButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected WrappedPredicateButton.PredicateClickButton createButton(Map<String, Object> section) {
    Map<String, Object> keys = MapUtils.createLowercaseStringObjectMap(section);
    WrappedSimpleButton simpleButton = new WrappedSimpleButton(new ButtonBuilder.Input(getMenu(), getName(), section));
    PredicateButton predicateButton = new PredicateButton();
    predicateButton.setButton(simpleButton);
    return WrappedPredicateButton.getPredicateButton(keys, this, checked, predicateButton);
  }

  @Override
  public void refresh(UUID uuid) {
    checked.remove(uuid);
    if (this.button == null) {
      return;
    }
    Button tempButton = ((WrappedPredicateButton.PredicateClickButton) this.button).getPredicateButton().getButton();
    if (tempButton instanceof MenuButton) {
      ((MenuButton) tempButton).refresh(uuid);
    }
  }

  @Override
  public StringReplacer getStringReplacer() {
    return (LookupStringReplacer) original -> {
      if (this.button == null) {
        return null;
      }
      WrappedPredicateButton.PredicateClickButton predicateButton = (WrappedPredicateButton.PredicateClickButton) this.button;
      if (original.startsWith("button")) {
        Button button = predicateButton.getPredicateButton().getButton();
        if (button instanceof MenuButton) {
          return Pair.of(((MenuButton) button).getStringReplacer(), original.substring("button".length()));
        }
      }
      if (original.startsWith("click") && predicateButton.getClickRequirements() != null) {
        return Pair.of(predicateButton.getClickRequirements().getStringReplacer(), original.substring("click".length()));
      }
      if (original.startsWith("view") && predicateButton.getViewRequirement() != null) {
        return Pair.of(predicateButton.getViewRequirement().getStringReplacer(), original.substring("view".length()));
      }
      return null;
    };
  }
}
