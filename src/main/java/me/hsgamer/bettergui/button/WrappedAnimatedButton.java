package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.bukkit.gui.Button;
import me.hsgamer.hscore.bukkit.gui.button.AnimatedButton;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.hscore.common.Validate;
import org.simpleyaml.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WrappedAnimatedButton extends BaseWrappedButton {
  private List<WrappedButton> wrappedButtonList;

  /**
   * Create a new button
   *
   * @param menu the menu
   */
  public WrappedAnimatedButton(Menu menu) {
    super(menu);
  }

  @Override
  protected Button createButton(ConfigurationSection section) {
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section.getValues(false));
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

    AnimatedButton animatedButton = new AnimatedButton(BetterGUI.getInstance(), update, async);
    Optional.ofNullable(keys.get("child"))
      .filter(o -> o instanceof ConfigurationSection)
      .map(o -> ButtonBuilder.INSTANCE.getChildButtons(this, (ConfigurationSection) o))
      .ifPresent(frames -> {
        wrappedButtonList = frames;
        frames.forEach(animatedButton::addChildButtons);
      });
    return animatedButton;
  }

  @Override
  public void refresh(UUID uuid) {
    if (wrappedButtonList != null) {
      wrappedButtonList.forEach(button -> button.refresh(uuid));
    }
  }
}
