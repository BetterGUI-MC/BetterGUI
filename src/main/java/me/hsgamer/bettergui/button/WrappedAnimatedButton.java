package me.hsgamer.bettergui.button;

import io.github.projectunified.craftux.button.AnimatedButton;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.util.TickUtil;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Validate;

import java.math.BigDecimal;
import java.util.*;

public class WrappedAnimatedButton extends BaseWrappedButton<AnimatedButton> {
  public WrappedAnimatedButton(ButtonBuilder.Input input) {
    super(input);
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

    List<WrappedButton> frames = Optional.ofNullable(keys.get("child"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .map(o -> ButtonBuilder.INSTANCE.getChildButtons(this, o))
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
      this.button.getButtons().stream().filter(WrappedButton.class::isInstance).forEach(button -> ((WrappedButton) button).refresh(uuid));
    }
  }
}
