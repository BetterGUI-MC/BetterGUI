package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.button.impl.AnimatedButton;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.Validate;

import java.math.BigDecimal;
import java.util.*;

public class WrappedAnimatedButton extends BaseWrappedButton {
  /**
   * Create a new button
   *
   * @param menu the menu
   */
  public WrappedAnimatedButton(Menu menu) {
    super(menu);
  }

  @Override
  protected Button createButton(Map<String, Object> section) {
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section);
    long update = Optional.ofNullable(keys.get("update"))
      .map(String::valueOf)
      .flatMap(Validate::getNumber)
      .filter(bigDecimal -> bigDecimal.compareTo(BigDecimal.ZERO) > 0)
      .map(BigDecimal::longValue)
      .orElse(0L);
    boolean async = Optional.ofNullable(keys.get("async"))
      .map(String::valueOf)
      .map(Boolean::parseBoolean)
      .orElse(true);
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
      .filter(o -> o instanceof Map)
      .map(o -> (Map<String, Object>) o)
      .map(o -> ButtonBuilder.INSTANCE.getChildButtons(this, o))
      .orElse(Collections.emptyList());
    frames = CollectionUtils.rotate(frames, shift);
    if (reverse) {
      frames = CollectionUtils.reverse(frames);
    }

    AnimatedButton animatedButton = new AnimatedButton(BetterGUI.getInstance(), update, async);
    frames.forEach(animatedButton::addChildButtons);
    return animatedButton;
  }

  @Override
  public void refresh(UUID uuid) {
    if (this.button instanceof AnimatedButton) {
      ((AnimatedButton) this.button).getButtons().stream().filter(button -> button instanceof WrappedButton).forEach(button -> ((WrappedButton) button).refresh(uuid));
    }
  }
}
