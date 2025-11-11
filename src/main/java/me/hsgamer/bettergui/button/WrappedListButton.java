package me.hsgamer.bettergui.button;

import io.github.projectunified.craftux.button.ListButton;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.MapUtils;

import java.util.*;

public class WrappedListButton extends BaseWrappedButton<ListButton> {
  public WrappedListButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected ListButton createButton(Map<String, Object> section) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);
    boolean keepCurrentIndex = Optional.ofNullable(keys.get("keep-current-index")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false);
    List<WrappedButton> childButtons = Optional.ofNullable(keys.get("child"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .map(o -> ButtonBuilder.INSTANCE.getChildButtons(this, o))
      .orElseGet(Collections::emptyList);

    ListButton button = new ListButton();
    button.addButton(childButtons);
    button.setKeepCurrentIndex(keepCurrentIndex);
    return button;
  }

  @Override
  public void refresh(UUID uuid) {
    if (this.button != null) {
      this.button.removeCurrentIndex(uuid);
      this.button.getButtons().stream().filter(WrappedButton.class::isInstance).forEach(button -> ((WrappedButton) button).refresh(uuid));
    }
  }
}
