package me.hsgamer.bettergui.button;

import io.github.projectunified.craftux.button.AnimatedButton;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.MenuButton;
import me.hsgamer.bettergui.api.replacer.ElementLookupStringReplacer;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.util.TickUtil;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.common.Validate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class WrappedAnimatedButton extends MenuButton {
  private final BetterGUI plugin;

  public WrappedAnimatedButton(BetterGUI plugin, ButtonBuilder.Input input) {
    super(input);
    this.plugin = plugin;
  }

  @Override
  protected AnimatedButton createButton(Map<String, Object> section) {
    Map<String, Object> keys = MapUtils.createLowercaseStringObjectMap(section);
    long update = Optional.ofNullable(keys.get("update"))
      .map(String::valueOf)
      .flatMap(TickUtil::toMillis)
      .filter(n -> n > 0)
      .orElse(50L);
    int shift = Optional.ofNullable(keys.get("shift"))
      .map(String::valueOf)
      .flatMap(Validate::getNumber)
      .map(BigDecimal::intValue)
      .orElse(0);
    boolean reverse = Optional.ofNullable(keys.get("reverse"))
      .map(String::valueOf)
      .map(Boolean::parseBoolean)
      .orElse(false);

    List<MenuButton> frames = Optional.ofNullable(keys.get("child"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .map(o -> plugin.get(ButtonBuilder.class).getChildButtons(this, o))
      .orElse(Collections.emptyList());
    frames = CollectionUtils.rotate(frames, shift);
    if (reverse) {
      frames = CollectionUtils.reverse(frames);
    }

    AnimatedButton animatedButton = new AnimatedButton();
    animatedButton.addButton(frames);
    animatedButton.setPeriodMillis(update);
    return animatedButton;
  }

  @Override
  public void refresh(UUID uuid) {
    if (this.button != null) {
      ((AnimatedButton) this.button).getButtons().stream().filter(MenuButton.class::isInstance).forEach(button -> ((MenuButton) button).refresh(uuid));
    }
  }

  @Override
  public StringReplacer getStringReplacer() {
    return new ElementLookupStringReplacer<MenuButton>() {
      @Override
      public List<MenuButton> getElements() {
        if (WrappedAnimatedButton.this.button != null) {
          return ((AnimatedButton) WrappedAnimatedButton.this.button).getButtons().stream().filter(MenuButton.class::isInstance).map(MenuButton.class::cast).collect(Collectors.toList());
        } else {
          return Collections.emptyList();
        }
      }

      @Override
      public String getPrefix() {
        return "child_";
      }
    };
  }
}
